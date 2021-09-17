package com.example.mygallery.ui.albums

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.model.unsplash.UCollection
import com.example.mygallery.databinding.FragmentAlbumsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AlbumsFragment : Fragment(), AlbumsAdapter.AlbumClickListener {

    companion object {
        fun newInstance() = AlbumsFragment()

        private const val ALBUMS_GRID_SPAN = 2
        private const val COLLECTION_PAGE_SIZE = 16
    }

    private val viewModel: AlbumsViewModel by viewModels()

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    private val albums: MutableList<UCollection> = mutableListOf()
    private val albumsAdapter by lazy {
        AlbumsAdapter(albums, this)
    }

    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var currentPage: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
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
        if (albums.isEmpty()) viewModel.getCollections(1, COLLECTION_PAGE_SIZE)
    }

    private fun configureUI() {
        val gridLayoutManager = GridLayoutManager(requireContext(), ALBUMS_GRID_SPAN)

        binding.fragmentAlbumsRV.layoutManager = gridLayoutManager
        binding.fragmentAlbumsRV.adapter = albumsAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.collectionsFlow.collect { it ->
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { collections ->
                            if (collections.isEmpty()) {
                                isLastPage = true
                            } else {
                                isLastPage = collections.size != COLLECTION_PAGE_SIZE

                                if (albums.isEmpty()) {
                                    albums.addAll(it.data)
                                    albumsAdapter.notifyItemRangeInserted(0, it.data.size)
                                } else {
                                    val prevSize = albums.size
                                    albums.addAll(it.data)
                                    albumsAdapter.notifyItemRangeInserted(prevSize, albums.size)
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
            val size = albums.size
            albums.clear()
            albumsAdapter.notifyItemRangeRemoved(0, size)
            currentPage = 1
            isLastPage = false
            isLoading = true
            viewModel.getCollections(1, COLLECTION_PAGE_SIZE)
        }

    }

    override fun albumClicked(album: UCollection) {
        findNavController().navigate(
            AlbumsFragmentDirections.actionAlbumsFragmentToPhotosFragment(album.id, album.title)
        )
    }

    override fun lastAlbumReached() {
        if (!isLoading && !isLastPage) {
            binding.root.isRefreshing = true
            isLoading = true
            currentPage += 1
            viewModel.getCollections(currentPage, COLLECTION_PAGE_SIZE)
        }
    }

}