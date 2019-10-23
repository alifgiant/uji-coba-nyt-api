package com.linkaja.exam.repository

import com.linkaja.exam.model.ArticleResponse
import com.linkaja.exam.model.BaseResult
import com.linkaja.exam.service.Api
import retrofit2.http.GET
import retrofit2.http.Query

object ArticleRepository {

    suspend fun getArticles(query: String, page: Long) = Api
        .retrofit
        .create(Service::class.java)
        .getArticles(query, page)

    interface Service {
        @GET("articlesearch.json")
        suspend fun getArticles(
            @Query(value = "q") query: String = "indonesia",
            @Query(value = "page") page: Long = 1,
            @Query(value = "api-key") key: String = "9b693ffaa5fe451090146e5c90fbed78"
        ): BaseResult<ArticleResponse>
    }
}