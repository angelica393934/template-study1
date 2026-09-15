package bsb.dev.bsb_bangking_jp.feature.change_email.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ChangeEmailApiService {

    @PUT("v1/dashboard/changeemail")
    suspend fun changeEmail(
        @HeaderMap headers: Map<String, String>,
        @Body body: ChangeEmailRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ChangeEmailResponse>

    @POST("v1/dashboard/confirmchangeemail")
    suspend fun confirmChangeEmail(
        @HeaderMap headers: Map<String, String>,
        @Body body: ConfirmChangeEmailRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ConfirmChangeEmailResponse>
}