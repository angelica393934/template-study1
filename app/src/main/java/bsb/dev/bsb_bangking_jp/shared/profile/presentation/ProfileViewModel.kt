package bsb.dev.bsb_bangking_jp.shared.profile.presentation

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * single (bukan viewModel) -- sama seperti BerandaViewModel dulu: 1 instance untuk
 * seluruh app supaya cache profile bertahan lintas halaman (Beranda, Pengaturan, dll).
 */
class ProfileViewModel(
    private val repository: ProfileRepository,
    private val photoRepository: ProfilePhotoRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(forceRefresh: Boolean = false) {
        scope.launch {
            if (!forceRefresh && repository.hasProfile) {
                _uiState.update {
                    it.copy(isLoading = false, profile = repository.cachedProfile, error = null)
                }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.getProfile(forceRefresh)
                _uiState.update { it.copy(isLoading = false, profile = result, error = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = (e as? ApiException)?.respMessage
                            ?: e.message
                            ?: "Terjadi kendala saat menampilkan profil Anda.",
                    )
                }
            }
        }
    }

    /** Padanan `GetProfilePhotoEvent.fetch(photoPath: ...)`. */
    private fun loadPhoto(photoPath: String?) {
        scope.launch {
            _uiState.update { it.copy(isPhotoLoading = true) }
            val bytes = try {
                photoRepository.getProfilePhoto(photoPath)
            } catch (e: Exception) {
                null
            }
            _uiState.update { it.copy(isPhotoLoading = false, photoBytes = bytes) }
        }
    }

    /** Dipanggil saat logout -- padanan reset state di BerandaViewModel.logout() lama. */
    fun clear() {
        _uiState.value = ProfileUiState()
    }
}