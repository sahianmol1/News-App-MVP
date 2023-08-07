package com.example.mvppractice.view.selectcountry

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvppractice.R
import com.example.mvppractice.contracts.SelectCountryContract
import com.example.mvppractice.model.SelectCountryModel
import com.example.mvppractice.presenter.SelectCountryPresenter
import com.example.mvppractice.view.models.CountriesUIModel
import kotlinx.coroutines.launch
import java.io.InputStream

class SelectCountryActivity : AppCompatActivity(), SelectCountryContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountriesAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var countriesJsonStream: InputStream
    private lateinit var model: SelectCountryModel
    private lateinit var presenter: SelectCountryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        recyclerView = findViewById(R.id.rv_countries)
        progressBar = findViewById(R.id.progress_bar)

        adapter = CountriesAdapter(
            onCountrySelected = {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        )

        countriesJsonStream = resources.openRawResource(R.raw.countries)

        model = SelectCountryModel(
            countriesJsonStream = countriesJsonStream
        )

        presenter = SelectCountryPresenter(
            view = this,
            model = model
        )

        lifecycleScope.launch {
            presenter.fetchCountriesList()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    override fun onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onCountriesListFetched(list: List<CountriesUIModel>) {
        progressBar.visibility = View.GONE
        adapter.submitList(list)
    }

    override fun onError(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }
}