package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.presentation

import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.BannerItem

sealed interface GetBannerUiState {
    data object Initial : GetBannerUiState
    data object Loading : GetBannerUiState
    data class Success(val banners: List<BannerItem>) : GetBannerUiState
    data class Error(val respCode: String, val message: String) : GetBannerUiState
}