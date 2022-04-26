package com.example.upa_app.data.db

import androidx.room.Entity
import com.example.upa_app.model.unsplash.UnsplashPhotoUrls
import com.example.upa_app.model.unsplash.UnsplashUser

@Entity(tableName = "unsplash")
data class UnsplashEntity (
    val id: String,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser
)