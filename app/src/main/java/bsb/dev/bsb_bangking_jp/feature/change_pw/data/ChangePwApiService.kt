package bsb.dev.bsb_bangking_jp.feature.change_pw.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ChangePwApiService {

    @POST("v1/dashboard/inputoldpassword")
    suspend fun validateOldPw(
        @HeaderMap headers: Map<String, String>,
        @Body body: ValidateOldPwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ValidateOldPwResponse>

    @PUT("v1/dashboard/changepassword")
    suspend fun changePw(
        @HeaderMap headers: Map<String, String>,
        @Body body: ChangePwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ChangePwResponse>

    @POST("v1/dashboard/verify-otp-changepassword")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: VerifyOtpChangePwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<VerifyOtpChangePwResponse>

    @POST("v1/resend-otp-change/changepasscode")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ResendOtpChangePwRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ResendOtpChangePwResponse>
}