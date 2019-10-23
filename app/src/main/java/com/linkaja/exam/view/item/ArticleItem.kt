package com.linkaja.test.view.item

import android.content.Context
import android.content.res.Configuration
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linkaja.test.R
import com.linkaja.test.model.Article
import com.linkaja.test.view.ext.cardView
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.configuration
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.padding
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
            if (article.multimedias != null && article.multimedias.isNotEmpty()) {
                val imageView = itemView.find<ImageView>(ID_IMAGE)
                Glide.with(itemView)
                    .load("https://static01.nyt.com/${article.multimedias.first().url}")
                    .placeholder(R.drawable.ic_under_construction)
                    .into(imageView)
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
                    val imageView = imageView {
                        image = ctx.getDrawable(R.drawable.ic_under_construction)
                        id = ID_IMAGE
                        scaleType = ImageView.ScaleType.CENTER_CROP

                    }.lparams(
                        width = ViewGroup.LayoutParams.MATCH_PARENT,
                        height = getImageHeight(ctx)
                    )
                    val title = textView {
                        text = "Title"
                        id = ID_TITLE
                        // textSize = sp(16)
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    val byline = textView {
                        text = "BYLINE"
                        id = ID_BYLINE
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    val date = textView {
                        text = "DATE"
                        id = ID_DATE
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                    val snippet = textView {
                        text = "Snippet"
                        id = ID_SNIPPET
                        textAlignment = TextView.TEXT_ALIGNMENT_TEXT_START
                    }
                }
            }
        }
    }

    companion object {
        const val ID_TITLE = 1
        const val ID_BYLINE = 2
        const val ID_DATE = 3
        const val ID_SNIPPET = 4
        const val ID_IMAGE = 5
    }
}

