package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain

interface GetBannerRepository {
    val hasData: Boolean
    val cachedBanners: List<BannerItem>?
    suspend fun getBanner(forceRefresh: Boolean = false): List<BannerItem>
}