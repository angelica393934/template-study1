package bsb.dev.bsb_bangking_jp.feature.beranda.presentation

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.session.SessionClearer
import bsb.dev.bsb_bangking_jp.feature.beranda.domain.get_banner.GetBannerRepository
import bsb.dev.bsb_bangking_jp.shared.profile.presentation.ProfileViewModel
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sekarang cuma bertanggung jawab atas Banner + orkestrasi refreshAll()/logout()
 * lintas ViewModel shared (Profile & RekeningLainnya). Ini BUKAN pemilik data
 * profile/rekening lagi -- itu ada di ProfileViewModel & RekeningLainnyaViewModel.
 */
class BerandaViewModel(
    private val profileViewModel: ProfileViewModel,
    private val rekeningViewModel: RekeningLainnyaViewModel,
    private val bannerRepository: GetBannerRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(BerandaUiState())
    val uiState: StateFlow<BerandaUiState> = _uiState.asStateFlow()

    fun loadBanner(forceRefresh: Boolean = false) {
        scope.launch {
            if (!forceRefresh && bannerRepository.hasData) {
                _uiState.update {
                    it.copy(isBannerLoading = false, bannerList = bannerRepository.cachedBanners, bannerError = null)
                }
                return@launch
            }

            _uiState.update { it.copy(isBannerLoading = true, bannerError = null) }
            try {
                val result = bannerRepository.getBanner(forceRefresh)
                _uiState.update { it.copy(isBannerLoading = false, bannerList = result, bannerError = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isBannerLoading = false,
                        bannerError = (e as? ApiException)?.respMessage ?: e.message ?: "Gagal memuat banner",
                    )
                }
            }
        }
    }

    /** Dipertahankan supaya call-site di BerandaPage tidak perlu tahu detail 3 ViewModel. */
    fun refreshAll() {
        profileViewModel.loadProfile()
        rekeningViewModel.load(forceRefresh = true)
        loadBanner()
    }
    /**
     * Padanan reset state lokal Beranda SETELAH logout sukses (bukan proses logout
     * itu sendiri). Dipanggil dari BerandaPage.onLoggedOut, SETELAH SessionManager
     * .clearSession() dijalankan oleh LogoutViewModel.
     */
    fun resetLocalState() {
        profileViewModel.clear()
        rekeningViewModel.clear()
        _uiState.update { BerandaUiState() }
    }
}