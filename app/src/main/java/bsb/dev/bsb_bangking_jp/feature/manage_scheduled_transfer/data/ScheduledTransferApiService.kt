package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Tag

interface ScheduledTransferApiService {

    @GET("v1/dashboard/getscheduledtransfer")
    suspend fun getScheduledTransfers(
        @HeaderMap headers: Map<String, String>,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ScheduledTransferListResponse>

    @POST("v1/dashboard/getdetailscheduledtransfer")
    suspend fun getScheduledTransferDetail(
        @HeaderMap headers: Map<String, String>,
        @Body body: ScheduledTransferIdRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ScheduledTransferDetailResponse>

    @PUT("v1/dashboard/togglepause")
    suspend fun togglePause(
        @HeaderMap headers: Map<String, String>,
        @Body body: ScheduledTransferIdRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ScheduledTransferActionResponse>

    // Retrofit tidak punya @DELETE dengan body -> pakai @HTTP manual (hasBody = true),
    // sama seperti SavedRecipientApiService.deleteSavedRecipient.
    @HTTP(method = "DELETE", path = "v1/dashboard/deletescheduledtransfer", hasBody = true)
    suspend fun deleteScheduledTransfer(
        @HeaderMap headers: Map<String, String>,
        @Body body: DeleteScheduledTransferRequest,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ScheduledTransferActionResponse>
}