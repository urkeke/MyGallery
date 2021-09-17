package com.example.mygallery.data.model

data class Photo(
    val id: Int,
    val name: String,
    val thumbnail: String?,
    val fullSize: String?,
    val creationDate: String?,
    val size: Long
)
