package com.linkaja.test.view.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.linkaja.test.model.Article
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val ui: MainUI by lazy {
        MainUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        viewModel.articleLiveData.observe(this, articleObserver)
    }

    private val articleObserver = Observer<List<Article>> { articles ->
        ui.setArticles(articles)
    }

    class MainVM : ViewModel() {
        val articleLiveData: MutableLiveData<List<Article>> by lazy {
            MutableLiveData<List<Article>>()
        }

        fun getArticles(query: String, page: Long) {
            articleLiveData.value = listOf()
        }
    }

    class MainUI : AnkoComponent<MainActivity> {
        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            recyclerView {
                layoutManager = GridLayoutManager(ctx, 1)
            }
        }

        fun setArticles(articles: List<Article>) {
        }
    }
}
