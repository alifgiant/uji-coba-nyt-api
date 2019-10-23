package com.linkaja.exam.view.fragment

import android.content.Context
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
import com.linkaja.exam.model.Article
import com.linkaja.exam.model.ArticleResponse
import com.linkaja.exam.model.BaseResult
import com.linkaja.exam.repository.ArticleRepository
import com.linkaja.exam.view.ext.recyclerView
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
        val resultObserver = Observer<BaseResult<ArticleResponse>> { result ->
            if (result.status == "OK" && result.response?.docs != null) {
                ui.addArticles(result.response.docs)
            } else {
                ui.showSnackBar("Gagal mengambil data")
            }
        }

        viewModel.resultLiveData.observe(this, resultObserver)
        viewModel.onCreate()
    }

    class HomeViewModel : ViewModel(), CoroutineScope {
        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

        val resultLiveData: MutableLiveData<BaseResult<ArticleResponse>> by lazy {
            MutableLiveData<BaseResult<ArticleResponse>>()
        }

        private var isFirstLoad = true
        private var page: Long = 1
        private var query: String = DEFAULT_QUERY

        fun onCreate() {
            if (isFirstLoad) getNextArticles()
        }

        fun getNextArticles() = launch(Dispatchers.IO) {
            val articles = ArticleRepository.getArticles(query, page)
            withContext(Dispatchers.Main) {
                if (articles.response?.docs != null && articles.response.docs.isNotEmpty())
                    isFirstLoad = false
                resultLiveData.value = articles
            }
        }

        fun onSearch(newQuery: String) {
            query = if (newQuery.isNotEmpty()) newQuery else DEFAULT_QUERY
            page = 1
        }

        companion object {
            const val DEFAULT_QUERY = "indonesia"
        }
    }

    class HomeUI(
        private val onSearch: (String) -> Unit,
        private val onScroll: () -> Unit
    ) : AnkoComponent<HomeFragment> {
        private lateinit var recyclerView: RecyclerView
        private lateinit var emptyView: View
        private lateinit var textView: TextView

        private val mainAdapter: ArticleItem.Adapter by lazy {
            ArticleItem.Adapter()
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

                    layoutManager = StaggeredGridLayoutManager(
                        getSpanCount(ctx),
                        StaggeredGridLayoutManager.VERTICAL
                    )
                    adapter = mainAdapter
                    visibility = View.GONE
                    horizontalPadding = dip(12)
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

        fun addArticles(newArticles: List<Article>) {
            mainAdapter.addArticles(newArticles)

            if (newArticles.isNotEmpty()) {
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