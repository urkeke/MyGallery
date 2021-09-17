package com.example.mygallery.data.api.repos

import com.example.mygallery.data.api.ApiInterface
import com.example.mygallery.data.api.BaseDataSource
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.model.Photo
import com.example.mygallery.data.model.unsplash.UPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    private val apiInterface: ApiInterface
) : BaseDataSource() {

    suspend fun getAlbumPhotos(albumId: Int): Flow<Resource<List<Photo>>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getAlbumPhotos(albumId) }
            emit(result)
        }
    }

    suspend fun getCollectionPhotos(collectionId: String, page: Int, size: Int): Flow<Resource<List<UPhoto>>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getCollectionPhotos(collectionId, page, size) }
            emit(result)
        }
    }

}