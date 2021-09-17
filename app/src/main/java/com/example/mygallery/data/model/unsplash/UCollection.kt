package com.example.mygallery.data.model.unsplash

import com.google.gson.annotations.SerializedName

data class UCollection(
    val id: String,
    val title: String,
    @SerializedName("cover_photo")
    val coverPhoto: UPhoto
)

