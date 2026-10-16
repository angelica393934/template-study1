package bsb.dev.bsb_bangking_jp.core.network.token.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface RefreshTokenApiService {
    @GET("v1/refresh-token/init")
    suspend fun refreshInitToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/initregistration")
    suspend fun refreshRegistToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/validactivation")
    suspend fun refreshActivationToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/login")
    suspend fun refreshLoginToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/forgetiduser")
    suspend fun refreshForgetIdUserToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>

    @GET("v1/refresh-token/forgetpasscode")
    suspend fun refreshForgetPwUserToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>
}
