package bsb.dev.bsb_bangking_jp.feature.registration.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Tag

interface RegistrationApiService {

    @POST("regist/getaccount")
    suspend fun getAccount(
        @HeaderMap headers: Map<String, String>,
        @Body body: GetAccountRequest,
    ): Response<GetAccountResponse>

    @POST("regist/resend-otp")
    suspend fun resendOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: RegistResendOtpRequest,
    ): Response<RegistResendOtpResponse>

    @POST("regist/verify-otp-registration")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: RegistVerifyOtpRequest,
    ): Response<RegistVerifyOtpResponse>

    @POST("regist/verify-device")
    suspend fun verifyDevice(
        @HeaderMap headers: Map<String, String>,
        @Body body: RegistVerifyDeviceRequest,
    ): Response<RegistVerifyDeviceResponse>

    @POST("v1/activation/addiduserlogin")
    suspend fun addIdUser(
        @HeaderMap headers: Map<String, String>,
        @Body body: AddIdUserRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<AddIdUserResponse>

    @POST("v1/activation/addpasscode")
    suspend fun addPasscode(
        @HeaderMap headers: Map<String, String>,
        @Body body: AddPasscodeRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<AddPasscodeResponse>
}