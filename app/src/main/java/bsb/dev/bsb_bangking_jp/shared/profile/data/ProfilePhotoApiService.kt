package bsb.dev.bsb_bangking_jp.shared.profile.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Tag

interface ProfilePhotoApiService {
    @Streaming
    @GET("v1/dashboard/getprofilephoto")
    suspend fun getProfilePhoto(
        @HeaderMap headers: Map<String, String>,
        @Query("path") path: String,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<ResponseBody>
}