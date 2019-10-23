package com.linkaja.exam.view.item

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linkaja.exam.R
import com.linkaja.exam.model.Article
import com.linkaja.exam.view.activity.NewsActivity
import com.linkaja.exam.view.ext.cardView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.configuration
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.space
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import java.util.Date

class ArticleItem {
    class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val articles = mutableListOf<Article>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ArticleUI().createView(AnkoContext.create(parent.context, parent)))
        }

        override fun getItemCount(): Int = articles.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(articles[position])
        }

        fun addArticles(newArticles: List<Article>) {
            articles.addAll(newArticles)
            notifyItemInserted(articles.size - newArticles.size)
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private fun getFormattedDate(rawDate: String?): String {
            val date = rawDate?.let { Article.RAW_DATE_FORMAT.parse(it) } ?: Date()
            return Article.SIMPLE_DATE_FORMAT.format(date)
        }

        fun bind(article: Article) {
            val imageView = itemView.find<ImageView>(ID_IMAGE)
            if (article.multimedias != null && article.multimedias.isNotEmpty()) {
                Glide.with(itemView)
                    .load("https://static01.nyt.com/${article.multimedias.first().url}")
                    .placeholder(R.drawable.ic_under_construction)
                    .into(imageView)
            } else {
                imageView.image = itemView.context.getDrawable(R.drawable.ic_under_construction)
            }
            itemView.find<TextView>(ID_TITLE).text = article.headline?.printHeadline
                ?: article.headline?.main
            itemView.find<TextView>(ID_BYLINE).text = article.byLine?.original

            itemView.find<TextView>(ID_DATE).text = getFormattedDate(article.pubDate)
            itemView.find<TextView>(ID_SNIPPET).text = article.snippet
        }
    }

    class ArticleUI : AnkoComponent<ViewGroup> {

        private fun getImageHeight(ctx: Context): Int {
            return when (ctx.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> ctx.dip(110)
                else -> ctx.dip(260)
            }
        }

        override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
            // frameLayout {
            cardView {
                useCompatPadding = true
                verticalLayout {
                    padding = dip(8)
                    gravity = Gravity.CENTER
                    imageView {
                        image = ctx.getDrawable(R.drawable.ic_under_construction)
                        id = ID_IMAGE
                        scaleType = ImageView.ScaleType.CENTER_CROP

                    }.lparams(
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = getImageHeight(ctx)
                    )
                    space().lparams(height = dip(12))
                    textView {
                        id = ID_TITLE
                        typeface = Typeface.DEFAULT_BOLD
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    textView {
                        id = ID_BYLINE
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    textView {
                        id = ID_DATE
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    space().lparams(height = dip(16))
                    textView {
                        id = ID_SNIPPET
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                }
                onClick {
                    startActivity<NewsActivity>(
                        "id" to 5,
                        "city" to "Denpasar"
                    )
                }
            }
        }
    }

    companion object {
        @IdRes
        val ID_TITLE = View.generateViewId()
        @IdRes
        val ID_BYLINE = View.generateViewId()
        @IdRes
        val ID_DATE = View.generateViewId()
        @IdRes
        val ID_SNIPPET = View.generateViewId()
        @IdRes
        val ID_IMAGE = View.generateViewId()
    }
}

