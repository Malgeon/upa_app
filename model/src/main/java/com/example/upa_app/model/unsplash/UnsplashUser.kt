package com.example.upa_app.model.unsplash

data class UnsplashUser(
    val name: String,
    val username: String
) {
    val attributionUrl: String
        get() {
            return "https://unsplash.com/$username?utm_source=sunflower&utm_medium=referral"
        }
}