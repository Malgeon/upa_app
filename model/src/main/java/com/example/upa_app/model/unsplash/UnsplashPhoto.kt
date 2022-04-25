package com.example.upa_app.model.unsplash

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser
)