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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.linkaja.exam.R
import com.linkaja.exam.ext.recyclerView
import com.linkaja.exam.repository.ArticleRepository
import com.linkaja.exam.view.item.ArticleItem
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.configuration
import org.jetbrains.anko.dip
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.horizontalPadding
import org.jetbrains.anko.image
import org.jetbrains.anko.imageView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.verticalPadding

class FavoriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    val ui: FavoriteUI by lazy {
        FavoriteUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ui.createView(AnkoContext.Companion.create(ctx, this))

    override fun onResume() {
        super.onResume()
        ui.recyclerView.adapter?.notifyDataSetChanged()
    }

    class FavoriteUI : AnkoComponent<FavoriteFragment> {
        lateinit var recyclerView: RecyclerView
        private lateinit var emptyView: View
        private lateinit var textView: TextView

        private val mainAdapter: ArticleItem.Adapter by lazy {
            ArticleItem.Adapter(isFavoriteScreen = true)
        }

        private fun getSpanCount(ctx: Context) = when (ctx.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 1
        }

        override fun createView(ui: AnkoContext<FavoriteFragment>) = with(ui) {
            val favorites = ArticleRepository.getFavorites()
            frameLayout {
                emptyView = verticalLayout {
                    visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
                    imageView {
                        image = ctx.getDrawable(R.drawable.ic_sun)
                    }.lparams(width = dip(210), height = dip(120))
                    textView = textView {
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
                    adapter = mainAdapter
                    visibility = if (favorites.isEmpty()) View.GONE else View.VISIBLE
                    horizontalPadding = dip(12)
                }
                backgroundColor = R.color.colorAccent
            }
        }
    }
}
