package com.linkaja.test.model

import com.google.gson.annotations.SerializedName

class BaseResult<T>(
    @SerializedName("status") val status: String,
    @SerializedName("copyright") val copyright: String,
    @SerializedName("response") val response: T
)

class Response(
    @SerializedName("docs") val docs: List<Article>,
    @SerializedName("meta") val meta: Meta
)

class Meta(
    @SerializedName("hits") val hits: Long,
    @SerializedName("offset") val offset: Long,
    @SerializedName("time") val time: Long
)

class Article()

// class Article(
//     @SerializedName("_id") val id: String,
//     @SerializedName("byline") val byLine: ByLine,
//     @SerializedName("document_type") val docType: String,
//     @SerializedName("headline") val headlines: List<HeadLine>,
//     @SerializedName("keyword") val keywords: List<Keyword>,
//     @SerializedName("multimedia") val multimedias: List<Multimedia>,
//     @SerializedName("news_desk") val newsDeck: String,
//     @SerializedName("print_page") val printPage: Long,
//     @SerializedName("pub_date") val pubDate: String,
//     @SerializedName("score") val score: Long,
//     @SerializedName("snippet") val snippet: String,
//     @SerializedName("source") val source: String,
//     @SerializedName("type_of_material") val typeOfMaterial: String,
//     @SerializedName("uri") val uri: String,
//     @SerializedName("web_url") val webUrl: String,
//     @SerializedName("word_count") val wordCount: Long,
//     @SerializedName("lead_paragraph") val leadParagraph: String,
//     @SerializedName("abstract") val abstract: String,
//     @SerializedName("section_name") val sectionName: String,
//     @SerializedName("subsection_name") val subSectionName: String
// )

class ByLine()
class HeadLine()
class Keyword()
class Multimedia()