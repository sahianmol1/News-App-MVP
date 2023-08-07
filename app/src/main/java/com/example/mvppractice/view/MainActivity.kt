package com.example.mvppractice.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvppractice.R
import com.example.mvppractice.contracts.MainActivityContract
import com.example.mvppractice.model.MainModel
import com.example.mvppractice.presenter.MainPresenter
import com.example.mvppractice.service.api.NewsApi
import com.example.mvppractice.view.adapter.TopHeadlinesAdapter
import com.example.mvppractice.view.models.TopHeadlinesUiModel
import com.example.mvppractice.view.selectcountry.SelectCountryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : FragmentActivity(), MainActivityContract.View {

    @Inject
    lateinit var api: NewsApi
    private lateinit var model: MainModel
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: TopHeadlinesAdapter

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var clSelectCountry: ConstraintLayout

    private val job = SupervisorJob()
    private val mainScope = CoroutineScope(job + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_top_headlines)
        loadingIndicator = findViewById(R.id.loading_bar)
        clSelectCountry = findViewById(R.id.cl_select_country)

        adapter = TopHeadlinesAdapter()

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = adapter

        model = MainModel(api)
        presenter = MainPresenter(model = model, view = this)

        mainScope.launch {
            try {
                presenter.getTopHeadlines(country = "us")
            } catch (e: Exception) {
                withContext(Dispatchers.Main.immediate) {
                    Toast.makeText(
                        this@MainActivity,
                        e.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        clSelectCountry.setOnClickListener {
            val intent = Intent(this, SelectCountryActivity::class.java)
            startActivity(intent)
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.main_container, SelectCountryFragment())
//            fragmentTransaction.commit()
        }

        getCountriesData()
    }

    private fun getCountriesData() {
        val inputStream = resources.openRawResource(R.raw.countries)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        inputStream.use {
            val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        }

        val jsonString: String = writer.toString()


        Log.i("getCountries Tag", "Countries from json: $jsonString")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    override fun onLoading() {
        Log.i("MainActivity", "OnLoading: List is loading")
        loadingIndicator.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
    }

    override fun onTopHeadlinesFetched(list: List<TopHeadlinesUiModel>) {
        mainScope.launch(Dispatchers.Main.immediate) {
            adapter.submitList(list)
            loadingIndicator.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onError(message: String) {
        mainScope.launch(Dispatchers.Main.immediate) {
            Toast.makeText(this@MainActivity, "Error: $message", Toast.LENGTH_LONG).show()

            loadingIndicator.visibility = View.GONE
            mRecyclerView.visibility = View.GONE
        }
    }
}