package com.linkaja.exam.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewManager
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit = {}): RecyclerView {
    return ankoView({ RecyclerView(it) }, theme = 0, init = init)
}

fun <T> AnkoContext<T>.cardView(init: CardView.() -> Unit = {}): CardView {
    return ankoView({ CardView(it) }, theme = 0, init = init)
}

fun EditText.afterTextChanged(timeMillis: Long = 500, afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        var job: Job? = null
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            job?.cancel()
            job = GlobalScope.launch {
                delay(timeMillis)
                afterTextChanged.invoke(editable.toString())
            }
        }
    })
}