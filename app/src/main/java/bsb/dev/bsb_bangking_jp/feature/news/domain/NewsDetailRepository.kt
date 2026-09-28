package bsb.dev.bsb_bangking_jp.feature.news.domain

import java.util.Date

data class NewsDetail(
    val id: String,
    val subtitle: String,
    val description: String,
    val pathImage: String,
    val createdDate: String,
    val targetUrl: String?,
)

interface NewsDetailRepository {
    suspend fun getNewsDetail(id: Int, forceRefresh: Boolean = false): NewsDetail
}