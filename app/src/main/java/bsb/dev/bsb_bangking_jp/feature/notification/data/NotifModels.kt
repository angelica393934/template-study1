package bsb.dev.bsb_bangking_jp.feature.notification.data

import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem
import com.google.gson.annotations.SerializedName

data class GetNotifResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: List<NotifItemDto> = emptyList(),
)

data class NotifItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("subtitle") val subtitle: String = "",
    @SerializedName("date") val date: String = "",
    @SerializedName("pathimage") val pathImage: String = "",
)

fun NotifItemDto.toDomain(): NotifItem = NotifItem(
    id = id,
    name = name,
    description = description,
    subtitle = subtitle,
    date = date,
    pathImage = pathImage,
)