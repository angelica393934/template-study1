package bsb.dev.bsb_bangking_jp.feature.change_mpin.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ChangeMpinApiService {

    @POST("v1/dashboard/inputoldmpin")
    suspend fun validateOldMpin(
        @HeaderMap headers: Map<String, String>,
        @Body body: ValidateOldMpinRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ValidateOldMpinResponse>

    @PUT("v1/dashboard/changempin")
    suspend fun changeMpin(
        @HeaderMap headers: Map<String, String>,
        @Body body: ChangeMpinRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ChangeMpinResponse>

    @POST("v1/dashboard/verify-otp-changempin")
    suspend fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @Body body: VerifyOtpChangeMpinRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<VerifyOtpChangeMpinResponse>
}