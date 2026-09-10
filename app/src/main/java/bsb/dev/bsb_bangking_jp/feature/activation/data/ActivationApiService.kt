package bsb.dev.bsb_bangking_jp.feature.activation.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Tag

interface ActivationApiService {

    @POST("v1/activation/getaccountactivation")
    suspend fun getAccountActivation(
        @HeaderMap headers: Map<String, String>,
        @Body body: GetAccountActivationRequest,
    ): Response<GetAccountActivationResponse>

    @POST("v1/activation/validationuseridforactivation")
    suspend fun validateUserId(
        @HeaderMap headers: Map<String, String>,
        @Body body: ValidateUserIdRequest,
    ): Response<ValidateUserIdResponse>

    @POST("v1/resend-otp/validuseridactivation")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ActivationResendOtpRequest,
    ): Response<ActivationResendOtpResponse>

    @POST("v1/activation/verify-otp-useridactivation")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: ActivationVerifyOtpRequest,
    ): Response<ActivationVerifyOtpResponse>

    @POST("v1/activation/validationpasscodeforactivation")
    suspend fun validatePasscode(
        @HeaderMap headers: Map<String, String>,
        @Body body: ValidatePasscodeRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ValidatePasscodeResponse>

    @POST("v1/activation/activation-confirmmpin")
    suspend fun confirmMpin(
        @HeaderMap headers: Map<String, String>,
        @Body body: ActivationConfirmMpinRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ActivationConfirmMpinResponse>
}