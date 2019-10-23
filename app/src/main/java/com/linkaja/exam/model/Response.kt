package com.linkaja.exam.model

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

val articlesType = object : TypeToken<List<Article>>() {}.type!!

data class BaseResult<T>(
    @SerializedName("status") val status: String? = null,
    @SerializedName("copyright") val copyright: String? = null,
    @SerializedName("response") val response: T? = null
)

data class ArticleResponse(
    @SerializedName("docs") val docs: List<Article>? = null,
    @SerializedName("meta") val meta: Meta? = null
)

data class Meta(
    @SerializedName("hits") val hits: Long? = null,
    @SerializedName("offset") val offset: Long? = null,
    @SerializedName("time") val time: Long? = null
)

data class Article(
    @SerializedName("_id") val id: String,
    @SerializedName("byline") val byLine: ByLine? = null,
    @SerializedName("document_type") val docType: String? = null,
    @SerializedName("headline") val headline: HeadLine? = null,
    @SerializedName("keywords") val keywords: List<Keyword>? = null,
    @SerializedName("multimedia") val multimedias: List<Multimedia>? = null,
    @SerializedName("news_desk") val newsDeck: String? = null,
    @SerializedName("print_page") val printPage: Long? = null,
    @SerializedName("pub_date") val pubDate: String? = null,
    @SerializedName("score") val score: Long? = null,
    @SerializedName("snippet") val snippet: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("type_of_material") val typeOfMaterial: String? = null,
    @SerializedName("uri") val uri: String? = null,
    @SerializedName("web_url") val webUrl: String? = null,
    @SerializedName("word_count") val wordCount: Long? = null,
    @SerializedName("lead_paragraph") val leadParagraph: String? = null,
    @SerializedName("abstract") val abstract: String? = null,
    @SerializedName("section_name") val sectionName: String? = null,
    @SerializedName("subsection_name") val subSectionName: String? = null,
    var isFavorite: Boolean = false
) {
    companion object {
        val RAW_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        val SIMPLE_DATE_FORMAT = SimpleDateFormat("MMM. dd, yyyy", Locale.US)
    }
}

data class ByLine(
    @SerializedName("organization") val organization: String? = null,
    @SerializedName("original") val original: String? = null,
    @SerializedName("person") val persons: List<Person>? = null
)

data class Person(
    @SerializedName("firstname") val firstName: String? = null,
    @SerializedName("lastname") val lastName: String? = null,
    @SerializedName("middlename") val middleName: String? = null,
    @SerializedName("organization") val organization: String? = null,
    @SerializedName("qualifier") val qualifier: String? = null,
    @SerializedName("rank") val rank: Long? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("title") val title: String? = null
)

data class HeadLine(
    @SerializedName("content_kicker") val contentKicker: String? = null,
    @SerializedName("kicker") val kicker: String? = null,
    @SerializedName("main") val main: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("print_headline") val printHeadline: String? = null,
    @SerializedName("seo") val seo: String? = null,
    @SerializedName("sub") val sub: String? = null
)

data class Keyword(
    @SerializedName("major") val major: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("rank") val rank: Long? = null,
    @SerializedName("value") val value: String? = null
)

data class Multimedia(
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("credit") val credit: String? = null,
    @SerializedName("crop_name") val cropName: String? = null,
    @SerializedName("height") val height: Long? = null,
    @SerializedName("legacy") val legacy: Size? = null,
    @SerializedName("rank") val rank: Long? = null,
    @SerializedName("subtype") val subtype: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("width") val width: Long
)

data class Size(
    @SerializedName("xlarge") val xlarge: String? = null,
    @SerializedName("xlargeheight") val xlargeHeight: Long? = null,
    @SerializedName("xlargewidth") val xlargeWidth: Long? = null
)
