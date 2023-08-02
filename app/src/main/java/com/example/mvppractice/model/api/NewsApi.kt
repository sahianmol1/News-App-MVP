package com.example.mvppractice.model.api

import retrofit2.http.GET

interface NewsApi {

    @GET("top-headlines")
    fun getTopHeadlines(country: String = "us")
}