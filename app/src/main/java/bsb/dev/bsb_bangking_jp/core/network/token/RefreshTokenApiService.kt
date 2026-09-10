// core/network/token/RefreshTokenApiService.kt
package bsb.dev.bsb_bangking_jp.core.network.token

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface RefreshTokenApiService {
    @GET("v1/refresh-token/init")
    suspend fun refreshInitToken(
        @HeaderMap headers: Map<String, String>,
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/initregistration")
    suspend fun refreshRegistToken(
        @HeaderMap headers: Map<String, String>,
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/validactivation")
    suspend fun refreshActivationToken(
        @HeaderMap headers: Map<String, String>,
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/login")
    suspend fun refreshLoginToken(
        @HeaderMap headers: Map<String, String>,
    ): Response<RefreshTokenResponse>
}

data class RefreshTokenResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: RefreshTokenData? = null,
)

data class RefreshTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)