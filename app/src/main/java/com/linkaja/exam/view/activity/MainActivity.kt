package com.linkaja.test.view.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.linkaja.test.R
import com.linkaja.test.model.Article
import com.linkaja.test.model.ArticleResponse
import com.linkaja.test.model.BaseResult
import com.linkaja.test.repository.ArticleRepository
import com.linkaja.test.view.ext.recyclerView
import com.linkaja.test.view.item.ArticleItem
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
import org.jetbrains.anko.design.bottomNavigationView
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.verticalPadding
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val ui: MainUI by lazy {
        MainUI(
            onSearch = { viewModel.onSearch(it) },
            onScroll = { viewModel.getNextArticles() }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

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

    class MainVM : ViewModel(), CoroutineScope {
        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

    }

    class MainUI(
        private val onSearch: (String) -> Unit,
        private val onScroll: () -> Unit
    ) : AnkoComponent<MainActivity> {
        // view instance
        private lateinit var recyclerView: RecyclerView
        private lateinit var emptyView: View
        private lateinit var textView: TextView

        private val mainAdapter: ArticleItem.Adapter by lazy {
            ArticleItem.Adapter()
        }

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            verticalLayout {
                frameLayout {
                    emptyView = verticalLayout {
                        visibility = View.VISIBLE
                        imageView {
                            image = ctx.getDrawable(R.drawable.ic_sun)
                        }.lparams(width = dip(210), height = dip(120))
                        textView = textView {
                            text = ctx.getString(R.string.caption_loading)
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
                }.lparams(
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    weight = 1f
                )
                bottomNavigationView {
                    inflateMenu(R.menu.menu_home)
                }.lparams(
                    width = ViewGroup.LayoutParams.MATCH_PARENT,
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                )
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
