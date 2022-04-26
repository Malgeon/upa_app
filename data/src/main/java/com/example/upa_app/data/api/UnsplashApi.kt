package com.example.upa_app.data.api

import com.example.upa_app.model.unsplash.UnsplashPhoto
import com.example.upa_app.model.unsplash.UnsplashSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("photos/random")
    suspend fun searchRandomPhotos(
        @Query("count") count: Int,
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): UnsplashSearchResponse
}