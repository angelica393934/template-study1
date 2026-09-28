package bsb.dev.bsb_bangking_jp.feature.news.data

import bsb.dev.bsb_bangking_jp.core.network.BaseRespCodeResponse
import bsb.dev.bsb_bangking_jp.feature.news.domain.NewsDetail
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GetNewsByIdRequest(
    @SerializedName("id") val id: Int,
)

data class NewsDetailResponse(
    @SerializedName("respCode") override val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: NewsDetailDto = NewsDetailDto(),
) : BaseRespCodeResponse

data class NewsDetailDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("subtitle") val subtitle: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("pathimage") val pathImage: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("targeturl") val targetUrl: String? = null,
)

fun NewsDetailDto.toDomain(): NewsDetail = NewsDetail(
    id = id,
    subtitle = subtitle,
    description = description,
    pathImage = pathImage,
    createdDate = date,
    targetUrl = targetUrl,
)