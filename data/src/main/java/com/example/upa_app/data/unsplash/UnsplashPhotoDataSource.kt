package com.example.upa_app.data.unsplash

import com.example.upa_app.model.unsplash.UnsplashPhotoData

interface UnsplashPhotoDataSource {
    fun getRemotePhotoData(): UnsplashPhotoData?
}

