package com.example.mvppractice.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mvppractice.R
import com.example.mvppractice.contracts.MainActivityContract
import com.example.mvppractice.model.MainModel
import com.example.mvppractice.presenter.MainPresenter
import com.example.mvppractice.service.api.NewsApi
import com.example.mvppractice.view.models.TopHeadlinesUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityContract.View {

    @Inject
    lateinit var api: NewsApi
    private lateinit var model: MainModel
    private lateinit var presenter: MainPresenter

    private val job = SupervisorJob()
    private val mainScope = CoroutineScope(job + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    override fun onLoading() {
        Log.i("MainActivity", "OnLoading: List is loading")
    }

    override fun onTopHeadlinesFetched(list: List<TopHeadlinesUiModel>) {
        mainScope.launch(Dispatchers.Main.immediate) {
            Toast.makeText(this@MainActivity, "List Size: ${list.size}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onError(message: String) {
        mainScope.launch(Dispatchers.Main.immediate) {
            Toast.makeText(this@MainActivity, "Error: $message", Toast.LENGTH_LONG).show()
        }
    }
}