package com.example.mygallery.data.api.repos

import com.example.mygallery.data.api.ApiInterface
import com.example.mygallery.data.api.BaseDataSource
import com.example.mygallery.data.api.Resource
import com.example.mygallery.data.model.Photo
import com.example.mygallery.data.model.unsplash.UPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PhotoPreviewRepository @Inject constructor(
    private val apiInterface: ApiInterface
) : BaseDataSource() {

    suspend fun getPhoto(photoId: Int) : Flow<Resource<Photo>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getPhoto(photoId) }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPhoto(photoId: String) : Flow<Resource<UPhoto>> {
        return flow {
            emit(Resource.Loading())
            val result = safeApiCall { apiInterface.getPhotoById(photoId) }
            emit(result)
        }
    }

}