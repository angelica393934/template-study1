package bsb.dev.bsb_bangking_jp.feature.notification.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Tag

interface NotifApiService {
    // Padanan GetNotifService.getNotif() -- GET dengan tokenPhase "login".
    @GET("v1/info/getnotif")
    suspend fun getNotif(
        @HeaderMap headers: Map<String, String>,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<GetNotifResponse>
}