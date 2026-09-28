// feature/beranda/data/BerandaApiService.kt
package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.data

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Tag

interface BerandaApiService {

    @GET("v1/info/getbanner")
    suspend fun getBanner(
        @HeaderMap headers: Map<String, String>,
        @Tag tokenPhase: TokenPhaseTag,
    ): Response<GetBannerResponse>
}