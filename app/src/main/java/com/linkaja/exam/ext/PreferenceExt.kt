package com.linkaja.exam.ext

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

fun Context.getPreference() = getSharedPreferences("link aja", Context.MODE_PRIVATE)

fun SharedPreferences.saveArticleString(raw: String) = edit {
    putString("articles", raw)
}

fun SharedPreferences.readArticleString() = getString("articles", null)