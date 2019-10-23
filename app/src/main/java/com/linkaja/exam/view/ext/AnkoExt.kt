package com.linkaja.exam.view.ext

import android.view.ViewManager
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit = {}): RecyclerView {
    return ankoView({ RecyclerView(it) }, theme = 0, init = init)
}

fun <T> AnkoContext<T>.cardView(init: CardView.() -> Unit = {}): CardView {
    return ankoView({ CardView(it) }, theme = 0, init = init)
}
