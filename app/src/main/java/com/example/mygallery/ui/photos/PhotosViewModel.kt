package com.example.mygallery.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.api.repos.PhotosRepository
import com.example.mygallery.data.model.Photo
import com.example.mygallery.data.model.unsplash.UPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repository: PhotosRepository
) : ViewModel() {

    private val _photosFlow = Channel<Resource<List<Photo>>>(Channel.BUFFERED)
    val photosFlow = _photosFlow.receiveAsFlow()

    private val _collectionPhotosFlow = Channel<Resource<List<UPhoto>>>(Channel.BUFFERED)
    val collectionPhotosFlow = _collectionPhotosFlow.receiveAsFlow()

    fun getAlbumPhotos(albumId: Int) {
        viewModelScope.launch {
            repository.getAlbumPhotos(albumId)
                .catch { e ->
                    _photosFlow.send(Resource.Error(e.toString()))
                }
                .collect { _photosFlow.send(it) }
        }
    }

    fun getCollectionPhotos(collectionId: String, page: Int, size: Int) {
        viewModelScope.launch {
            repository.getCollectionPhotos(collectionId, page, size)
                .catch { e ->
                    _collectionPhotosFlow.send(Resource.Error(e.toString()))
                }
                .collect { _collectionPhotosFlow.send(it) }
        }
    }

}