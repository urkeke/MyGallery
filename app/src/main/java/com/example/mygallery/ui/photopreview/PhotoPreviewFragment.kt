package com.example.mygallery.ui.photopreview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mygallery.data.api.Resource
import com.example.mygallery.databinding.FragmentPhotoPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PhotoPreviewFragment : Fragment() {

    companion object {
        fun newInstance() = PhotoPreviewFragment()
    }

    private val viewModel: PhotoPreviewViewModel by viewModels()

    private var _binding: FragmentPhotoPreviewBinding? = null
    private val binding get() = _binding!!

    private val args: PhotoPreviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoPreviewBinding.inflate(inflater, container, false)
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
        Log.d("mytag", args.photoId)
        viewModel.getPhoto(args.photoId)
    }

    private fun configureUI() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.photoName

        lifecycleScope.launchWhenStarted {
            viewModel.collectionPhotoFlow.collect {
                when (it) {
                    is Resource.Success -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = false
                        if (it.data != null) {
                            Glide.with(requireContext())
                                .load(it.data.urls.regular)
                                .into(binding.fragmentPhotoPreviewZoomIV)
                        }
                    }
                    is Resource.Loading -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = true
                    }
                    is Resource.Error -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = false
                    }
                }
            }
/*            viewModel.photoFlow.collect {
                when (it) {
                    is Resource.Success -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = false
                        if (it.data != null) {
                            Glide.with(requireContext())
                                .load(it.data.fullSize)
                                .into(binding.fragmentPhotoPreviewZoomIV)
                        }
                    }
                    is Resource.Loading -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = true
                    }
                    is Resource.Error -> {
                        binding.fragmentPhotoPreviewProgressBar.isVisible = false
                    }
                }
            }*/
        }
    }
}