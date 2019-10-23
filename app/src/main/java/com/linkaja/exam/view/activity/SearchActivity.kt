package com.linkaja.exam.view.activity

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.linkaja.exam.R
import com.linkaja.exam.ext.recyclerView
import com.linkaja.exam.model.Article
import com.linkaja.exam.repository.ArticleRepository
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
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.verticalPadding
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModels()
    private val ui: SearchUI by lazy {
        SearchUI(
            onSearch = { viewModel.search(it) },
            onScroll = { viewModel.getNextArticles() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        val resultObserver = Observer<List<Article>?> { result ->
            when {
                result == null -> ui.showSnackBar("Gagal mengambil data")
                result.isEmpty() -> ui.showSnackBar("Data sudah habis")
                else -> ui.updateArticle(result)
            }
        }

        viewModel.resultLD.observe(this, resultObserver)
    }

    class SearchViewModel : ViewModel(), CoroutineScope {
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

        fun search(query: String) {
        }
    }

    class SearchUI(
        private val onScroll: () -> Unit,
        private val onSearch: (String) -> Unit
    ) : AnkoComponent<SearchActivity> {

        private lateinit var recyclerView: RecyclerView
        private lateinit var emptyView: View

        private fun getSpanCount(ctx: Context) = when (ctx.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 1
        }

        override fun createView(ui: AnkoContext<SearchActivity>) = with(ui) {
            verticalLayout {
                editText {
                    hint = "Cari apa?"
                }
                frameLayout {
                    emptyView = verticalLayout {
                        // visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
                        imageView {
                            image = ctx.getDrawable(R.drawable.ic_sun)
                        }.lparams(width = dip(210), height = dip(120))
                        textView {
                            text = ctx.getString(R.string.caption_empty)
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
                        // adapter = mainAdapter
                        // visibility = if (favorites.isEmpty()) View.GONE else View.VISIBLE
                        horizontalPadding = dip(12)
                    }
                    backgroundColor = R.color.colorAccent
                }.lparams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = 0
                    weight = 1f
                }
            }
        }

        fun updateArticle(newArticles: List<Article>) {
            // if (newArticles.isNotEmpty()) {
            //     mainAdapter.notifyItemInserted(mainAdapter.itemCount - newArticles.size)
            //     emptyView.visibility = View.GONE
            //     recyclerView.visibility = View.VISIBLE
            // } else {
            //     textView.text = textView.resources.getString(R.string.caption_empty)
            // }
        }

        fun showSnackBar(msg: String) {
            recyclerView.snackbar(msg)
        }
    }
}
