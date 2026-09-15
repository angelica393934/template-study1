package bsb.dev.bsb_bangking_jp.feature.ganti_email.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface GantiEmailApiService {

    @PUT("v1/dashboard/changeemail")
    suspend fun gantiEmail(
        @HeaderMap headers: Map<String, String>,
        @Body body: GantiEmailRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<GantiEmailResponse>

    @POST("v1/dashboard/confirmchangeemail")
    suspend fun confirmGantiEmail(
        @HeaderMap headers: Map<String, String>,
        @Body body: ConfirmGantiEmailRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ConfirmGantiEmailResponse>
}