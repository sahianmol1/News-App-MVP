package com.example.mvppractice.presenter

import com.example.mvppractice.contracts.SelectCountryContract
import com.example.mvppractice.service.models.Country
import com.example.mvppractice.view.models.CountriesUIModel

class SelectCountryPresenter(
    private val view: SelectCountryContract.View,
    private val model: SelectCountryContract.Model
): SelectCountryContract.Model.OnFinishListener, SelectCountryContract.Presenter {

    override suspend fun fetchCountriesList() {
        return model.fetchCountriesList(this)
    }

    override fun onLoading() {
        view.onLoading()
    }

    override fun onSuccess(data: List<Country>) {
        view.onCountriesListFetched(
            list = data.map {
                CountriesUIModel(
                    countryName = it.countryName.orEmpty(),
                    countryCode = it.alpha2.orEmpty()
                )
            }
        )
    }

    override fun onError(message: String) {
        view.onError(message)
    }
}