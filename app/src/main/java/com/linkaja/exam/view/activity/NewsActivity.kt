package com.linkaja.exam.view.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.linkaja.exam.R
import com.linkaja.exam.ext.getPreference
import com.linkaja.exam.ext.recyclerView
import com.linkaja.exam.ext.saveArticleString
import com.linkaja.exam.model.Article
import com.linkaja.exam.repository.ArticleRepository
import com.linkaja.exam.service.Api
import com.linkaja.exam.view.EndlessRecyclerViewScrollListener
import com.linkaja.exam.view.item.ArticleItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.setContentView
import kotlin.coroutines.CoroutineContext

class NewsActivity : AppCompatActivity() {

    private val ui: NewsUI by lazy {
        NewsUI {
            viewModel.getNextArticles()
        }
    }

    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)


        val resultObserver = Observer<List<Article>?> { result ->
            when {
                result == null -> ui.showSnackBar("Gagal mengambil data")
                result.isEmpty() -> ui.showSnackBar("Data sudah habis")
                else -> {
                    ui.updateArticle(result)
                    saveData(result)
                }
            }
        }
        viewModel.resultLD.observe(this, resultObserver)
        intent.getStringExtra(KEY_POSITION)?.let { id ->
            val index = ArticleRepository.articles.indexOfFirst { it.id == id }
            if (index > 0) ui.recyclerView.scrollToPosition(index)
        }
    }

    private fun saveData(articlesResult: List<Article>) {
        val dataRaw = Api.gson.toJson(articlesResult)
        getPreference()?.saveArticleString(dataRaw)
    }

    class NewsViewModel : ViewModel(), CoroutineScope {
        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

        val resultLD: MutableLiveData<List<Article>?> by lazy {
            MutableLiveData<List<Article>?>()
        }

        fun getNextArticles(query: String = "") = launch {
            val articles = ArticleRepository.requestNextArticles(query)
            withContext(Dispatchers.Main) {
                resultLD.value = articles
            }
        }
    }

    class NewsUI(private val onScroll: () -> Unit) : AnkoComponent<NewsActivity> {
        lateinit var recyclerView: RecyclerView

        private val mainAdapter: ArticleItem.Adapter by lazy {
            ArticleItem.Adapter(isFullScreen = true)
        }

        private fun getScrollListener(layoutManager: RecyclerView.LayoutManager) =
            object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    onScroll()
                }
            }

        override fun createView(ui: AnkoContext<NewsActivity>) = with(ui) {
            frameLayout {
                recyclerView = recyclerView {
                    LinearSnapHelper().attachToRecyclerView(this)
                    adapter = mainAdapter
                    val mLayoutManager = LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false)
                    layoutManager = mLayoutManager
                    backgroundColor = R.color.colorAccent
                    addOnScrollListener(getScrollListener(mLayoutManager))
                }
            }
        }

        fun updateArticle(newArticles: List<Article>) {
            if (newArticles.isNotEmpty()) {
                mainAdapter.notifyItemInserted(mainAdapter.itemCount - newArticles.size)
                recyclerView.visibility = View.VISIBLE
            }
        }

        fun showSnackBar(msg: String) {
            recyclerView.snackbar(msg)
        }
    }

    companion object {
        const val KEY_POSITION = "article_id"
    }
}
