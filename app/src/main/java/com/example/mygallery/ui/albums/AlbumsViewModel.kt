package com.example.mygallery.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.api.repos.AlbumsRepository
import com.example.mygallery.data.model.Album
import com.example.mygallery.data.model.unsplash.UCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val repository: AlbumsRepository
) : ViewModel() {

    private val _albumsFlow = Channel<Resource<List<Album>>>(Channel.BUFFERED)
    val albumsFlow = _albumsFlow.receiveAsFlow()

    private val _collectionsFlow = Channel<Resource<List<UCollection>>>(Channel.BUFFERED)
    val collectionsFlow = _collectionsFlow.receiveAsFlow()

    fun getAlbums() {
        viewModelScope.launch {
            repository.getAlbums()
                .catch { e -> _albumsFlow.send(Resource.Error(e.toString())) }
                .collect { _albumsFlow.send(it) }
        }
    }

    fun getCollections(page: Int, size: Int) {
        viewModelScope.launch {
            repository.getCollections(page, size)
                .catch { e -> _collectionsFlow.send(Resource.Error(e.toString())) }
                .collect { _collectionsFlow.send(it) }
        }
    }

}