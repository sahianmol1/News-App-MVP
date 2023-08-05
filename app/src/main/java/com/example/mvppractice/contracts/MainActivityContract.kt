package com.example.mvppractice.contracts

import com.example.mvppractice.service.models.TopHeadlines
import com.example.mvppractice.view.models.TopHeadlinesUiModel

interface MainActivityContract {

    interface View {
        fun onLoading()
        fun onTopHeadlinesFetched(list: List<TopHeadlinesUiModel>)
        fun onError(message: String)
    }


    interface Presenter {
        suspend fun getTopHeadlines(country: String)
    }

    interface Model {
        suspend fun getTopHeadlines(country: String, onFinishListener: OnFinishListener)

        interface OnFinishListener {
            fun loading()
            fun success(data: TopHeadlines)
            fun error(message: String)
        }
    }
}