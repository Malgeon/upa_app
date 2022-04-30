package com.example.upa_app.data.unsplash

import com.example.upa_app.data.api.UnsplashApi
import com.example.upa_app.model.unsplash.UnsplashPhoto
import com.example.upa_app.model.unsplash.UnsplashPhotoData
import javax.inject.Inject

interface UnsplashPhotoDataSource {
    fun getRemotePhotoData(): UnsplashPhotoData?
}

class DefaultUnsplashPhotoDataSource @Inject constructor(
    private val service: UnsplashApi
) : UnsplashPhotoDataSource {


    override suspend fun getRemotePhotoData(): UnsplashPhotoData? {
        return getRandomPhotos(1).map {
            UnsplashPhotoData(it.id, it.urls, it.user)
        }
    }


    suspend fun getRandomPhotos(count: Int): List<UnsplashPhoto> {
        return service.randomPhotos(count)
    }

}