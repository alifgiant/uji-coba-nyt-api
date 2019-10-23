package com.linkaja.exam.repository

import android.content.SharedPreferences
import com.linkaja.exam.ext.readArticleString
import com.linkaja.exam.ext.readFavoriteArticleString
import com.linkaja.exam.ext.saveFavoriteArticleString
import com.linkaja.exam.model.Article
import com.linkaja.exam.model.ArticleResponse
import com.linkaja.exam.model.BaseResult
import com.linkaja.exam.model.articlesType
import com.linkaja.exam.service.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Query

object ArticleRepository {
    private const val DEFAULT_QUERY = "indonesia"
    private const val PAGE_START = -1L

    private var page = PAGE_START
    val articles: MutableList<Article> = mutableListOf()
    private val favoriteArticles: MutableList<Article> = mutableListOf()

    fun addRemoveFavorite(pref: SharedPreferences, isAdd: Boolean, article: Article) {
        if (isAdd) {
            favoriteArticles.add(article)
        } else {
            val index = favoriteArticles.indexOfFirst { it.id == article.id }
            if (index > -1) favoriteArticles.removeAt(index)
        }

        val rawString = Api.gson.toJson(favoriteArticles)
        pref.saveFavoriteArticleString(rawString)
    }

    fun getFavorites() = favoriteArticles.toList()

    suspend fun requestNextArticles(query: String): List<Article>? {
        try {
            val result = Api
                .retrofit
                .create(Service::class.java)
                .getArticles(if (query.isNotEmpty()) query else DEFAULT_QUERY, page + 1)

            return if (result.response?.docs != null && result.response.docs.isNotEmpty()) {
                page += 1
                if (page == 0L) articles.clear()
                withContext(Dispatchers.Main) {
                    articles.addAll(result.response.docs)
                }
                result.response.docs
            } else {
                listOf()
            }
        } catch (e: Exception) {
            page = PAGE_START
            return null
        }
    }

    fun loadCachedArticles(pref: SharedPreferences): List<Article>? {
        val rawFavoriteSavedResult = pref.readFavoriteArticleString()
        if (rawFavoriteSavedResult != null) {
            val savedResult: List<Article> = Api.gson.fromJson(rawFavoriteSavedResult, articlesType)
            favoriteArticles.addAll(savedResult)
        }

        val rawSavedResult = pref.readArticleString()
        return if (rawSavedResult != null) {
            val savedResult: List<Article> = Api.gson.fromJson(rawSavedResult, articlesType)
            articles.addAll(savedResult)
            savedResult
        } else {
            null
        }
    }

    fun reset() {
        page = PAGE_START
        articles.clear()
    }

    interface Service {
        @GET("articlesearch.json")
        suspend fun getArticles(
            @Query(value = "q") query: String = "indonesia",
            @Query(value = "page") page: Long = 1,
            @Query(value = "api-key") key: String = "9b693ffaa5fe451090146e5c90fbed78"
        ): BaseResult<ArticleResponse>
    }
}