package com.example.mvppractice.model

import com.example.mvppractice.contracts.MainActivityContract
import com.example.mvppractice.service.api.NewsApi

class MainModel(
    private val api: NewsApi
): MainActivityContract.Model {
    override suspend fun getTopHeadlines(
        country: String,
        onFinishListener: MainActivityContract.Model.OnFinishListener
    ) {
        onFinishListener.loading()
        try {
            val response = api.getTopHeadlines()
            if (response.isSuccessful) {
                response.body()?.let {
                    onFinishListener.success(it)
                } ?: onFinishListener.error("Response is empty")
            } else {
                onFinishListener.error("Something went wrong. Please try again later!")
            }
        } catch (e: Exception) {
            onFinishListener.error(e.message.toString())
        }
    }

}