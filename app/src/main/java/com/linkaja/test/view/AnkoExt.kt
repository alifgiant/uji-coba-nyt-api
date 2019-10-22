package com.linkaja.test.view

import android.view.ViewManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit = {}): RecyclerView {
    return ankoView({ RecyclerView(it) }, theme = 0, init = init)
}