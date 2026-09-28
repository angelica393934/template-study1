package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.data

import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.BannerItem
import com.google.gson.annotations.SerializedName

data class GetBannerResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: BannerResponseData = BannerResponseData(),
)

/** Padanan struktur baru: data -> banner -> List. */
data class BannerResponseData(
    @SerializedName("banner") val banner: List<BannerItemDto> = emptyList(),
)

data class BannerItemDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("subtitle") val subtitle: String = "",
    @SerializedName("type") val type: String = "",
)

fun BannerItemDto.toDomain(): BannerItem = BannerItem(
    name = name,
    description = description,
    subtitle = subtitle,
    type = type,
)