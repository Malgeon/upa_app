package com.example.upa_app.model.unsplash

data class UnsplashSearchResponse(
    val results: List<UnsplashPhoto>,
    val totalPages: Int
)