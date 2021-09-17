package com.example.mygallery.data.api.repos

import com.example.mygallery.data.api.ApiInterface
import com.example.mygallery.data.api.BaseDataSource
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.model.Album
import com.example.mygallery.data.model.unsplash.UCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AlbumsRepository @Inject constructor(
    private val apiInterface: ApiInterface
): BaseDataSource() {

    suspend fun getAlbums() : Flow<Resource<List<Album>>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getAlbums() }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCollections(page: Int, size: Int) : Flow<Resource<List<UCollection>>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getCollections(page, size) }
            emit(result)
        }
    }

}