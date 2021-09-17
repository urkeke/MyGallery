package com.example.mygallery.ui.photopreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.api.repos.PhotoPreviewRepository
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
class PhotoPreviewViewModel @Inject constructor(
    private val repository: PhotoPreviewRepository
) : ViewModel() {

    private val _photoFlow = Channel<Resource<Photo>>(Channel.BUFFERED)
    val photoFlow = _photoFlow.receiveAsFlow()

    private val _collectionPhotoFlow = Channel<Resource<UPhoto>>(Channel.BUFFERED)
    val collectionPhotoFlow = _collectionPhotoFlow.receiveAsFlow()

    fun getPhoto(photoId: Int) {
        viewModelScope.launch {
            repository.getPhoto(photoId)
                .catch { e -> _photoFlow.send(Resource.Error(e.toString())) }
                .collect { _photoFlow.send(it) }
        }
    }

    fun getPhoto(photoId: String) {
        viewModelScope.launch {
            repository.getPhoto(photoId)
                .catch { e -> _collectionPhotoFlow.send(Resource.Error(e.toString())) }
                .collect { _collectionPhotoFlow.send(it) }
        }
    }

}