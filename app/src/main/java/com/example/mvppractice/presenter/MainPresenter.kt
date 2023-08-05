package com.example.mvppractice.presenter

import com.example.mvppractice.contracts.MainActivityContract
import com.example.mvppractice.service.models.TopHeadlines
import com.example.mvppractice.view.models.TopHeadlinesUiModel

class MainPresenter(
    private val model: MainActivityContract.Model,
    private val view: MainActivityContract.View
) : MainActivityContract.Model.OnFinishListener,
    MainActivityContract.Presenter {
    override suspend fun getTopHeadlines(country: String) {
        model.getTopHeadlines(onFinishListener = this, country = country)
    }

    override fun loading() {
        view.onLoading()
    }

    override fun success(data: TopHeadlines) {
        view.onTopHeadlinesFetched(
            data.articles.map { article ->
                TopHeadlinesUiModel(
                    title = article.title ?: "",
                    description = article.description ?: "",
                    imageUrl = article.urlToImage ?: "",
                    url = article.url ?: ""
                )
            }
        )
    }

    override fun error(message: String) {
        view.onError(message)
    }
}