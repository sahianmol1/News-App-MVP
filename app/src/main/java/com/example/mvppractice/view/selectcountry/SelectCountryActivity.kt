package com.example.mvppractice.view.selectcountry

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvppractice.R
import com.example.mvppractice.common.utils.Constants
import com.example.mvppractice.contracts.SelectCountryContract
import com.example.mvppractice.model.SelectCountryModel
import com.example.mvppractice.presenter.SelectCountryPresenter
import com.example.mvppractice.view.models.CountriesUIModel
import kotlinx.coroutines.launch
import java.io.InputStream

class SelectCountryActivity : AppCompatActivity(), SelectCountryContract.View, SearchView.OnQueryTextListener {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CountriesAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var countriesJsonStream: InputStream
    private lateinit var model: SelectCountryModel
    private lateinit var presenter: SelectCountryPresenter

    private var countriesList: List<CountriesUIModel> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_country)

        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.rv_countries)
        progressBar = findViewById(R.id.progress_bar)

        adapter = CountriesAdapter(
            onCountrySelected = { country ->
                val intent = Intent()
                intent.putExtra(Constants.COUNTRY_CODE, country)
                setResult(RESULT_OK, intent)
                finish()
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

        setUpActionBar(toolbar)
    }

    private fun setUpActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.select_a_country)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        toolbar.inflateMenu(R.menu.menu_toolbar)
    }

    override fun onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onCountriesListFetched(list: List<CountriesUIModel>) {
        progressBar.visibility = View.GONE
        countriesList = list
        adapter.submitList(countriesList)
    }

    override fun onError(message: String) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)

        val search = menu?.findItem(R.id.action_search)
        val searchView = search?.actionView as? SearchView

        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            val filteredCountries = countriesList.filter {
                it.countryName.contains(query, true) || it.countryCode.contains(query, true)
            }

            adapter.submitList(filteredCountries)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            val filteredCountries = countriesList.filter {
                it.countryName.contains(newText, true) || it.countryCode.contains(newText, true)
            }

            adapter.submitList(filteredCountries)
        }
        return true
    }
}
