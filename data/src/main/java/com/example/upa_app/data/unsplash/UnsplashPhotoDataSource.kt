package com.example.upa_app.data.unsplash

import com.example.upa_app.data.api.UnsplashApi
import com.example.upa_app.model.unsplash.UnsplashPhoto
import com.example.upa_app.model.unsplash.UnsplashPhotoData
import javax.inject.Inject

interface UnsplashPhotoDataSource {
    suspend fun getRemotePhotoData(): UnsplashPhotoData?
}

class DefaultUnsplashPhotoDataSource @Inject constructor(
    private val service: UnsplashApi
) : UnsplashPhotoDataSource {


    override suspend fun getRemotePhotoData(): UnsplashPhotoData? {
        return UnsplashPhotoData(getRandomPhotos(1))
    }


    private suspend fun getRandomPhotos(count: Int): List<UnsplashPhoto> {
        return service.searchRandomPhotos(count)
    }

}