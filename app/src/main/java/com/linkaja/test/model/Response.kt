package com.linkaja.test.model

import com.google.gson.annotations.SerializedName

data class BaseResult<T>(
    @SerializedName("status") val status: String,
    @SerializedName("copyright") val copyright: String,
    @SerializedName("response") val response: T
)

data class ArticleResponse(
    @SerializedName("docs") val docs: List<Article>,
    @SerializedName("meta") val meta: Meta
)

data class Meta(
    @SerializedName("hits") val hits: Long,
    @SerializedName("offset") val offset: Long,
    @SerializedName("time") val time: Long
)

data class Article(
    @SerializedName("_id") val id: String,
    @SerializedName("byline") val byLine: ByLine,
    @SerializedName("document_type") val docType: String,
    @SerializedName("headline") val headlines: HeadLine,
    @SerializedName("keywords") val keywords: List<Keyword>,
    @SerializedName("multimedia") val multimedias: List<Multimedia>,
    @SerializedName("news_desk") val newsDeck: String,
    @SerializedName("print_page") val printPage: Long,
    @SerializedName("pub_date") val pubDate: String,
    @SerializedName("score") val score: Long,
    @SerializedName("snippet") val snippet: String,
    @SerializedName("source") val source: String,
    @SerializedName("type_of_material") val typeOfMaterial: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("web_url") val webUrl: String,
    @SerializedName("word_count") val wordCount: Long,
    @SerializedName("lead_paragraph") val leadParagraph: String,
    @SerializedName("abstract") val abstract: String,
    @SerializedName("section_name") val sectionName: String,
    @SerializedName("subsection_name") val subSectionName: String
)

data class ByLine(
    @SerializedName("organization") val organization: String? = null,
    @SerializedName("original") val original: String,
    @SerializedName("person") val persons: List<Person>
)

data class Person(
    @SerializedName("firstname") val firstName: String,
    @SerializedName("lastname") val lastName: String? = null,
    @SerializedName("middlename") val middleName: String? = null,
    @SerializedName("organization") val organization: String,
    @SerializedName("qualifier") val qualifier: String? = null,
    @SerializedName("rank") val rank: Long,
    @SerializedName("role") val role: String,
    @SerializedName("title") val title: String? = null
)

data class HeadLine(
    @SerializedName("content_kicker") val contentKicker: String? = null,
    @SerializedName("kicker") val kicker: String? = null,
    @SerializedName("main") val main: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("print_headline") val printHeadline: String,
    @SerializedName("seo") val seo: String? = null,
    @SerializedName("sub") val sub: String? = null
)

data class Keyword(
    @SerializedName("major") val major: String,
    @SerializedName("name") val name: String,
    @SerializedName("rank") val rank: Long,
    @SerializedName("value") val value: String
)

data class Multimedia(
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("credit") val credit: String? = null,
    @SerializedName("crop_name") val cropName: String,
    @SerializedName("height") val height: Long,
    @SerializedName("legacy") val legacy: Size,
    @SerializedName("rank") val rank: Long,
    @SerializedName("subtype") val subtype: String,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Long
)

data class Size(
    @SerializedName("xlarge") val xlarge: String,
    @SerializedName("xlargeheight") val xlargeHeight: Long,
    @SerializedName("xlargewidth") val xlargeWidth: Long
)
