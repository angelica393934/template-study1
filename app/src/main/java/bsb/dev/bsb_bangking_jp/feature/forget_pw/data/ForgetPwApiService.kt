package bsb.dev.bsb_bangking_jp.feature.forget_pw.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ForgetPwApiService {

    @POST("v1/getpasscode")
    suspend fun getPw(
        @HeaderMap headers: Map<String, String>,
        @Body body: GetPwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<GetPwResponse>

    @POST("v1/verify-otp-passcode")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ForgetPwVerifyOtpRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ForgetPwVerifyOtpResponse>

    @PUT("v1/dashboard/changepasscode")
    suspend fun changePw(
        @HeaderMap headers: Map<String, String>,
        @Body body: ChangePwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ChangePwResponse>

    @POST("v1/resend-otp/forgetpasscode")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ForgetPwResendOtpRequest,
    ): Response<ForgetPwResendOtpResponse>
}