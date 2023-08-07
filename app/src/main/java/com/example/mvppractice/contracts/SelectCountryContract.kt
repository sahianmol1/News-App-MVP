package com.example.mvppractice.contracts

import com.example.mvppractice.service.models.Country
import com.example.mvppractice.view.models.CountriesUIModel

interface SelectCountryContract {

    interface View {

        fun onLoading()
        fun onCountriesListFetched(list: List<CountriesUIModel>)

        fun onError(message: String)
    }

    interface Presenter {
        suspend fun fetchCountriesList()
    }

    interface Model {
        suspend fun fetchCountriesList(onFinishListener: OnFinishListener)

        interface OnFinishListener {
            fun onLoading()

            fun onSuccess(data: List<Country>)

            fun onError(message: String)
        }
    }
}