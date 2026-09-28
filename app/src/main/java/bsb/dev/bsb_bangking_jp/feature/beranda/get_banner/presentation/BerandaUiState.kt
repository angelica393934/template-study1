// feature/beranda/presentation/BerandaUiState.kt
package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.presentation

import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.BannerItem
data class BerandaUiState(
    val isBannerLoading: Boolean = false,
    val bannerList: List<BannerItem>? = null,
    val bannerError: String? = null,
)

sealed class BerandaUiEvent {
    data class ShowToastSuccess(val message: String) : BerandaUiEvent()
    data class ShowToastError(val message: String) : BerandaUiEvent()
}