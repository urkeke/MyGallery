package com.example.mygallery.data.api

import com.example.mygallery.data.model.Album
import com.example.mygallery.data.model.Photo
import com.example.mygallery.data.model.unsplash.UCollection
import com.example.mygallery.data.model.unsplash.UPhoto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("v3/0c3ce2b6-0fe0-41cd-9409-ad912acbe366")
    suspend fun getAlbums(): Response<List<Album>>

    @GET("v3/b8a10901-5801-4f5f-97b9-2a2a5bb5d959")
    suspend fun getAlbumPhotos(@Header("albumId") albumId: Int): Response<List<Photo>>

    @GET("v3/707ff365-86a4-4cb2-b4ad-5648b83cf0a0")
    suspend fun getPhoto(@Header("photoId") photoId: Int): Response<Photo>

    @GET("collections")
    suspend fun getCollections(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<UCollection>>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<UPhoto>>

    @GET("photos/{id}")
    suspend fun getPhotoById(
        @Path("id") photoId: String
    ): Response<UPhoto>
}