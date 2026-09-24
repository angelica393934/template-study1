package bsb.dev.bsb_bangking_jp.feature.login_existing.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Tag

interface LoginExistingApiService {
    @POST("v1/login/init")
    suspend fun loginInit(
        @HeaderMap headers: Map<String, String>,
        @Body body: LoginInitRequest,
    ): Response<LoginInitResponse>

    @POST("v1/verify-otp")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: VerifyOtpRequest,
    ): Response<VerifyOtpResponse>

    @POST("v1/resend-otp/existingakun")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ResendOtpRequest,
    ): Response<ResendOtpResponse>

    @POST("v1/verify-device")
    suspend fun verifyDevice(
        @HeaderMap headers: Map<String, String>,
        @Body body: VerifyDeviceRequest,
    ): Response<VerifyDeviceResponse>

    @POST("v1/confirm/confirmmpin")
    suspend fun confirmMpin(
        @HeaderMap headers: Map<String, String>,
        @Body body: ConfirmMpinRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ConfirmMpinResponse>
}