package com.example.upa_app.data.unsplash

import com.example.upa_app.model.unsplash.UnsplashPhotoData
import javax.inject.Inject

interface UnsplashPhotoDataSource {
    fun getRemotePhotoData(): UnsplashPhotoData?
}

class DefaultUnsplashPhotoDataSource @Inject constructor(

) : UnsplashPhotoDataSource {


    override fun getRemotePhotoData(): UnsplashPhotoData? {
        TODO("Not yet implemented")
    }

    private fun



}