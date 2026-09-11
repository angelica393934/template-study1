package bsb.dev.bsb_bangking_jp.feature.forget_iduser.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ForgetIdUserApiService {

    @POST("v1/getiduser")
    suspend fun getIdUser(
        @HeaderMap headers: Map<String, String>,
        @Body body: GetIdUserRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<GetIdUserResponse>

    @POST("v1/verify-otp-iduser")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ForgetIdVerifyOtpRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ForgetIdVerifyOtpResponse>

    @PUT("v1/dashboard/changeiduser")
    suspend fun changeIdUser(
        @HeaderMap headers: Map<String, String>,
        @Body body: ChangeIdUserRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ChangeIdUserResponse>

    @POST("v1/resend-otp/forgetuserid")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ForgetIdResendOtpRequest,
    ): Response<ForgetIdResendOtpResponse>
}