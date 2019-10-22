package com.linkaja.test.view.activity

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
import com.linkaja.test.view.recyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.textView
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainVM by viewModels()
    private val mainAdapter by lazy {
        MainAdater()
    }
    private val ui: MainUI by lazy {
        MainUI(mainAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        viewModel.articleLiveData.observe(this, articleObserver)
        viewModel.getArticles("indonesia", 1)
    }

    private val articleObserver = Observer<List<Article>> { Articles ->
        ui.addArticles(Articles)
    }

    class MainVM : ViewModel(), CoroutineScope {

        private var job: Job = SupervisorJob()

        override val coroutineContext: CoroutineContext
            get() = job + Dispatchers.Default

        val articleLiveData: MutableLiveData<List<Article>> by lazy {
            MutableLiveData<List<Article>>()
        }

        fun getArticles(query: String, page: Long) = launch {
            delay(3000)

            withContext(Dispatchers.Main) {
                articleLiveData.value = listOf(
                    Article(),
                    Article(),
                    Article(),
                    Article(),
                    Article(),
                    Article(),
                    Article(),
                    Article(),
                    Article()
                )
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

    class MainUI(private val mainAdapter: MainAdater) : AnkoComponent<MainActivity> {

        lateinit var recyclerView: RecyclerView
        lateinit var imageView: ImageView

        override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
            frameLayout {
                imageView = imageView {
                    image = ctx.getDrawable(R.drawable.ic_sun)
                    horizontalPadding = dip(72)
                    visibility = View.VISIBLE
                }
                    // .lparams(width = dip(28))
                recyclerView = recyclerView {
                    layoutManager = GridLayoutManager(ctx, 1)
                    adapter = mainAdapter
                    visibility = View.GONE
                }
            }

            recyclerView
        }

        fun addArticles(newArticles: List<Article>) {
            mainAdapter.addArticles(newArticles)
            imageView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        fun swapOrientation() {
        }
    }
}
