package com.linkaja.test.view.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkaja.test.R
import com.linkaja.test.model.Article
import com.linkaja.test.model.ArticleResponse
import com.linkaja.test.model.BaseResult
import com.linkaja.test.repository.ArticleRepository
import com.linkaja.test.view.recyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.textView
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val ui: MainUI by lazy {
        MainUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        viewModel.resultLiveData.observe(this, resultObserver)
        viewModel.onCreate()
    }

    private val resultObserver = Observer<BaseResult<ArticleResponse>> { result ->
        if (result.status == "OK") {
            ui.addArticles(result.response.docs)
        } else {
            ui.recyclerView.snackbar("Gagal mengambil data")
        }
    }

    class MainVM : ViewModel(), CoroutineScope {
        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

        val resultLiveData: MutableLiveData<BaseResult<ArticleResponse>> by lazy {
            MutableLiveData<BaseResult<ArticleResponse>>()
        }

        var isFirstLoad = true

        fun onCreate() {
            if (isFirstLoad)
                getArticles("indonesia", 1)
            isFirstLoad = false
        }

        fun getArticles(query: String, page: Long) = launch(Dispatchers.IO) {
            val articles = ArticleRepository.getArticles(query, page)
            withContext(Dispatchers.Main) {
                resultLiveData.value = articles
            }
        }
    }

    class MainVH(item: View) : RecyclerView.ViewHolder(item) {
        fun bind(Article: Article) {
        }
    }

    class MainAdater : RecyclerView.Adapter<MainVH>() {
        private val Articles = mutableListOf<Article>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainVH {
            val view = with(AnkoContext.create(parent.context)) {
                textView {
                    text = "asdsdsd"
                }
            }

            return MainVH(view)
        }

        override fun getItemCount(): Int = Articles.size

        override fun onBindViewHolder(holder: MainVH, position: Int) {
            holder.bind(Articles[position])
        }

        fun addArticles(newArticles: List<Article>) {
            Articles.addAll(newArticles)
            notifyItemInserted(Articles.size - newArticles.size)
        }
    }

    class MainUI : AnkoComponent<MainActivity> {
        // view instance
        lateinit var recyclerView: RecyclerView
        private lateinit var imageView: ImageView

        private val mainAdapter: MainAdater by lazy {
            MainAdater()
        }

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            frameLayout {
                imageView = imageView {
                    image = ctx.getDrawable(R.drawable.ic_sun)
                    // padding = dip(72)
                    width
                    visibility = View.VISIBLE
                }.lparams(width = dip(110), height = dip(92))
                recyclerView = recyclerView {
                    layoutManager = GridLayoutManager(ctx, getSpanCount(ctx))
                    adapter = mainAdapter
                    visibility = View.GONE
                }
            }

            recyclerView
        }

        private fun getSpanCount(ctx: Context) = when (ctx.resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 1
        }

        fun addArticles(newArticles: List<Article>) {
            mainAdapter.addArticles(newArticles)
            imageView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
