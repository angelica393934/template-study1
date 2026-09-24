package bsb.dev.bsb_bangking_jp.feature.login.data

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("useridlogin") val useridLogin: String,
    @SerializedName("passcode") val passcode: String,
)

data class LoginResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: LoginTokenData? = null,
)

data class LoginTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)