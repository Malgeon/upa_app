package com.example.upa_app.data.unsplash

import com.example.upa_app.model.unsplash.UnsplashPhotoData

interface UnsplashPhotoDataSource {
    fun getRemotePhotoData(): UnsplashPhotoData?
}

class DefaultUnsplashPhotoDataSource : UnsplashPhotoDataSource {


    override fun getRemotePhotoData(): UnsplashPhotoData? {
        TODO("Not yet implemented")
    }

}