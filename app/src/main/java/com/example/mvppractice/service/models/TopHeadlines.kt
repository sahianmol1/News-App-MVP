package com.example.mvppractice.service.models

data class TopHeadlines(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)