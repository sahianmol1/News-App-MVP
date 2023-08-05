package com.example.mvppractice.service.api

import com.example.mvppractice.BuildConfig
import com.example.mvppractice.service.models.TopHeadlines
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apikey: String = BuildConfig.NEWS_API_KEY
    ): Response<TopHeadlines>
}