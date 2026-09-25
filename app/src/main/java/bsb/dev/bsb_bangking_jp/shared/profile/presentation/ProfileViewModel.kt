package bsb.dev.bsb_bangking_jp.shared.profile.presentation

import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * single (bukan viewModel) -- sama seperti BerandaViewModel dulu: 1 instance untuk
 * seluruh app supaya cache profile bertahan lintas halaman (Beranda, Pengaturan, dll).
 */
class ProfileViewModel(
    private val repository: ProfileRepository,
    private val photoRepository: ProfilePhotoRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var photoJob: Job? = null
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(forceRefresh: Boolean = false) {
        scope.launch {
            if (!forceRefresh && repository.hasProfile) {
                val cached = repository.cachedProfile
                _uiState.update { it.copy(isLoading = false, profile = cached, error = null) }

                // 🔹 profil dari cache, tapi foto belum ada (mis. VM baru dibuat / foto gagal sebelumnya)
                // Aman dipanggil: ProfilePhotoRepositoryImpl sudah punya cache sendiri per path.
                if (_uiState.value.photoBytes == null) {
                    loadPhoto(cached?.user?.photoProfile)
                }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.getProfile(forceRefresh)
                _uiState.update { it.copy(isLoading = false, profile = result, error = null) }
                loadPhoto(result.user.photoProfile)
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
        photoJob?.cancel() // 🔹 hindari race kalau refresh dipanggil beruntun
        photoJob = scope.launch {
            // photoBytes lama sengaja TIDAK di-null-kan dulu, supaya avatar tidak berkedip
            _uiState.update { it.copy(isPhotoLoading = true) }
            val bytes = try {
                photoRepository.getProfilePhoto(photoPath)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            _uiState.update { it.copy(isPhotoLoading = false, photoBytes = bytes) }
        }
    }

    fun clear() {
        photoJob?.cancel()
        _uiState.value = ProfileUiState()
    }
}