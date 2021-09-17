package com.example.mygallery.ui.photos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.model.unsplash.UPhoto
import com.example.mygallery.databinding.FragmentPhotosBinding
import com.example.mygallery.ui.albums.AlbumsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PhotosFragment : Fragment(), PhotosAdapter.PhotoClickListener {

    companion object {
        fun newInstance() = PhotosFragment()

        private const val PHOTOS_GRID_SPAN = 3
        private const val COLLECTION_PHOTOS_PAGE_SIZE = 24
    }

    private val viewModel: PhotosViewModel by viewModels()

    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    private val args: PhotosFragmentArgs by navArgs()

    private val photos: MutableList<UPhoto> = mutableListOf()
    private val photosAdapter: PhotosAdapter by lazy {
        PhotosAdapter(photos, this)
    }

    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()
        configureUI()
    }

    private fun fetchData() {
        if (photos.isEmpty()) viewModel.getCollectionPhotos(
            args.collectionId,
            1,
            COLLECTION_PHOTOS_PAGE_SIZE
        )
    }

    private fun configureUI() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.collectionName

        binding.fragmentPhotosRV.layoutManager =
            GridLayoutManager(requireContext(), PHOTOS_GRID_SPAN)
        binding.fragmentPhotosRV.adapter = photosAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.collectionPhotosFlow.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { collections ->
                            if (collections.isEmpty()) {
                                isLastPage = true
                            } else {
                                isLastPage = collections.size != COLLECTION_PHOTOS_PAGE_SIZE

                                if (photos.isEmpty()) {
                                    photos.addAll(it.data)
                                    photosAdapter.notifyItemRangeInserted(0, it.data.size)
                                } else {
                                    val prevSize = photos.size
                                    photos.addAll(it.data)
                                    photosAdapter.notifyItemRangeInserted(prevSize, photos.size)
                                }
                            }
                        }
                        isLoading = false
                        binding.root.isRefreshing = false
                    }
                    is Resource.Loading -> {
                        isLoading = true
                        binding.root.isRefreshing = true
                    }
                    is Resource.Error -> {
                        isLoading = false
                        binding.root.isRefreshing = false
                    }
                }
            }
        }

        binding.root.setOnRefreshListener {
            val size = photos.size
            photos.clear()
            photosAdapter.notifyItemRangeRemoved(0, size)
            currentPage = 1
            isLastPage = false
            isLoading = true
            viewModel.getCollectionPhotos(args.collectionId, 1, COLLECTION_PHOTOS_PAGE_SIZE)
        }
    }

    override fun photoClicked(photo: UPhoto) {
        findNavController().navigate(
            PhotosFragmentDirections.actionPhotosFragmentToPhotoPreviewFragment(
                photo.id,
                photo.description ?: " "
            )
        )
    }

    override fun lastPhotoReached() {
        if (!isLoading && !isLastPage) {
            binding.root.isRefreshing = true
            isLoading = true
            currentPage += 1
            viewModel.getCollectionPhotos(
                args.collectionId,
                currentPage,
                COLLECTION_PHOTOS_PAGE_SIZE
            )
        }
    }
}