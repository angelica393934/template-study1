package bsb.dev.bsb_bangking_jp.core.network.token.data

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: RefreshTokenData? = null,
)

data class RefreshTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)