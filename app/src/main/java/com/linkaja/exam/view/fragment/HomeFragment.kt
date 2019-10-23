package com.linkaja.exam.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
import org.jetbrains.anko.configuration
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.verticalPadding
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val ui: HomeUI by lazy {
        HomeUI(
            onSearch = { viewModel.onSearch(it) },
            onScroll = { viewModel.getNextArticles() }
        )
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ui.createView(AnkoContext.Companion.create(ctx, this))

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
        viewModel.onCreate()
        viewModel.loadFromPref(context?.getPreference())
    }

    private fun saveData(articlesResult: List<Article>) {
        val dataRaw = Api.gson.toJson(articlesResult)
        context?.getPreference()?.saveArticleString(dataRaw)
    }

    class HomeViewModel : ViewModel(), CoroutineScope {
        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

        val resultLD: MutableLiveData<List<Article>?> by lazy {
            MutableLiveData<List<Article>?>()
        }

        var isFirstLoad = true

        fun onCreate() {
            if (isFirstLoad) {
                getNextArticles()
            }
        }

        fun loadFromPref(pref: SharedPreferences?) = launch {
            pref?.let {
                val article = ArticleRepository.loadCachedArticles(it)
                withContext(Dispatchers.Main) {
                    resultLD.value = article
                }
            }
        }

        fun getNextArticles(query: String = "") = launch {
            val articles = ArticleRepository.requestNextArticles(query)
            withContext(Dispatchers.Main) {
                if (articles != null && articles.isNotEmpty()) {
                    isFirstLoad = false
                }
                resultLD.value = articles
            }
        }

        fun onSearch(newQuery: String) {

            // load data
            getNextArticles()
        }
    }

    class HomeUI(
        private val onSearch: (String) -> Unit,
        private val onScroll: () -> Unit
    ) : AnkoComponent<HomeFragment> {
        private lateinit var recyclerView: RecyclerView
        private lateinit var emptyView: View
        private lateinit var textView: TextView
        private var isFirstLoad: Boolean = true

        private val mainAdapter: ArticleItem.Adapter by lazy {
            ArticleItem.Adapter()
        }

        private fun getScrollListener(layoutManager: RecyclerView.LayoutManager) =
            object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    onScroll()
                }
            }

        override fun createView(ui: AnkoContext<HomeFragment>) = with(ui) {
            frameLayout {
                emptyView = verticalLayout {
                    visibility = View.VISIBLE
                    imageView {
                        image = ctx.getDrawable(R.drawable.ic_sun)
                    }.lparams(width = dip(210), height = dip(120))
                    textView = textView {
                        text = ctx.getString(R.string.caption_loading)
                        textColor = Color.WHITE
                        gravity = Gravity.CENTER
                        verticalPadding = dip(26)
                    }
                }.lparams(gravity = Gravity.CENTER)
                recyclerView = recyclerView {
                    val mLayoutManager = StaggeredGridLayoutManager(
                        getSpanCount(ctx),
                        StaggeredGridLayoutManager.VERTICAL
                    )
                    layoutManager = mLayoutManager
                    adapter = mainAdapter
                    visibility = View.GONE
                    horizontalPadding = dip(12)
                    addOnScrollListener(getScrollListener(mLayoutManager))
                }
                floatingActionButton {
                    useCompatPadding = true
                    image = ctx.getDrawable(R.drawable.ic_search)
                    onClick { }
                }.lparams(
                    width = ViewGroup.LayoutParams.WRAP_CONTENT,
                    gravity = Gravity.BOTTOM + Gravity.END
                )
                backgroundColor = R.color.colorAccent
            }
        }

        private fun getSpanCount(ctx: Context) = when (ctx.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 1
        }

        fun updateArticle(newArticles: List<Article>) {
            if (newArticles.isNotEmpty()) {
                mainAdapter.notifyItemInserted(mainAdapter.itemCount - newArticles.size)
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                textView.text = textView.resources.getString(R.string.caption_empty)
            }
        }

        fun showSnackBar(msg: String) {
            recyclerView.snackbar(msg)
        }
    }
}
