package com.linkaja.test.view.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.linkaja.test.model.Article
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.verticalLayout

class ArticleItem {
    class Adapter : RecyclerView.Adapter<ViewHolder>() {
        private val articles by lazy { mutableListOf<Article>() }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ArticleUI().createView(
                    AnkoContext.Companion.create(
                        parent.context, parent
                    )
                )
            )
        }

        override fun getItemCount(): Int = articles.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(articles[position])

        fun addArticles(newArticles: List<Article>) {
            articles.addAll(newArticles)
            notifyItemInserted(articles.size - newArticles.size)
        }
    }

    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var ivImage: ImageView = itemView.findViewById(ArticleUI.ID_IMAGE)
        fun bind(article: Article) {
        }
    }

    class ArticleUI : AnkoComponent<ViewGroup> {

        override fun createView(ui: AnkoContext<ViewGroup>) = with(ui) {
            verticalLayout {

            }
        }

        companion object {
            const val ID_IMAGE = 1
        }
    }
}