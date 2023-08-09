package com.example.mvppractice.view.topheadlines

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
class MainActivity : AppCompatActivity(), MainActivityContract.View {

    @Inject
    lateinit var api: NewsApi
    private lateinit var model: MainModel
    private lateinit var presenter: MainPresenter
    private lateinit var adapter: TopHeadlinesAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.rv_top_headlines)
        loadingIndicator = findViewById(R.id.loading_bar)

        setupToolbar()

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

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val countryCode = intent?.getStringExtra(Constants.COUNTRY_CODE)
                    countryCode?.let {
                        lifecycleScope.launch {
                            adapter.submitList(emptyList())
                            presenter.getTopHeadlines(it)
                        }
                        supportActionBar?.title = getString(R.string.top_headlines, it)
                    }
                }
            }
    }

    private fun setupToolbar() {
        supportActionBar?.title = getString(R.string.top_headlines, INITIAL_COUNTRY)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_select_a_country) {
            val intent = Intent(this, SelectCountryActivity::class.java)
            resultLauncher.launch(intent)
        }
        return true
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