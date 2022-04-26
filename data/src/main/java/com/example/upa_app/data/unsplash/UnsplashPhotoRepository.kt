package com.example.upa_app.data.unsplash

import com.example.upa_app.data.db.UnsplashDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoRepository @Inject constructor(
    private val remoteDataSource: UnsplashPhotoDataSource,
    private val unsplashDao: UnsplashDao
) {
}