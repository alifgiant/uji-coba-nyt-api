package com.linkaja.exam.service

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    val gson: Gson by lazy { Gson() }

    val retrofit: Retrofit by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.nytimes.com/svc/search/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

