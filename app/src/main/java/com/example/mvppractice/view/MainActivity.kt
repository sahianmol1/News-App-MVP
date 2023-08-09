package com.example.mvppractice.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvppractice.R
import com.example.mvppractice.common.utils.Constants
import com.example.mvppractice.common.utils.Constants.INITIAL_COUNTRY
import com.example.mvppractice.contracts.MainActivityContract
import com.example.mvppractice.model.MainModel
import com.example.mvppractice.presenter.MainPresenter
import com.example.mvppractice.service.api.NewsApi
import com.example.mvppractice.view.adapter.TopHeadlinesAdapter
import com.example.mvppractice.view.models.TopHeadlinesUiModel
import com.example.mvppractice.view.selectcountry.SelectCountryActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var tvCountryName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_top_headlines)
        loadingIndicator = findViewById(R.id.loading_bar)
        clSelectCountry = findViewById(R.id.cl_select_country)
        tvCountryName = findViewById(R.id.tv_country_name)

        tvCountryName.text = INITIAL_COUNTRY

        adapter = TopHeadlinesAdapter(
            onArticleClicked = { url ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        )

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = adapter

        model = MainModel(api)
        presenter = MainPresenter(model = model, view = this)

        lifecycleScope.launch {
            try {
                presenter.getTopHeadlines(country = INITIAL_COUNTRY)
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

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val countryCode = intent?.getStringExtra(Constants.COUNTRY_CODE)
                    countryCode?.let {
                        tvCountryName.text = INITIAL_COUNTRY
                        lifecycleScope.launch {
                            adapter.submitList(emptyList())
                            presenter.getTopHeadlines(it)
                        }
                        tvCountryName.text = it
                    }
                }
            }

        clSelectCountry.setOnClickListener {
            val intent = Intent(this, SelectCountryActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onLoading() {
        Log.i("MainActivity", "OnLoading: List is loading")
        loadingIndicator.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
    }

    override fun onTopHeadlinesFetched(list: List<TopHeadlinesUiModel>) {
        lifecycleScope.launch {
            adapter.submitList(list)
            loadingIndicator.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onError(message: String) {
        lifecycleScope.launch {
            Toast.makeText(this@MainActivity, "Error: $message", Toast.LENGTH_LONG).show()

            loadingIndicator.visibility = View.GONE
            mRecyclerView.visibility = View.GONE
        }
    }
}