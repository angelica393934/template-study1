// feature/splash/SplashViewModel.kt
package bsb.dev.bsb_bangking_jp.feature.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.device.AppPreferences
import bsb.dev.bsb_bangking_jp.core.util.getAppVersion
import bsb.dev.bsb_bangking_jp.feature.init.domain.InitDeviceUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    application: Application,
    private val initDeviceUseCase: InitDeviceUseCase,
    private val appPreferences: AppPreferences,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    // 🔹 Channel (bukan SharedFlow) -- event di-BUFFER sampai ada consumer,
    // tidak hilang walau emit terjadi sebelum Composable mulai collect.
    private val _navigationEvent = Channel<SplashNavigationEvent>(Channel.BUFFERED)
    val navigationEvent: Flow<SplashNavigationEvent> = _navigationEvent.receiveAsFlow()

    val appVersion: String = getAppVersion(application) ?: "1.0.0"

    private var navigated = false

    init {
        checkDeviceInit()
    }

    private fun checkDeviceInit() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading

            if (appPreferences.isInitSuccess()) {
                checkStatus()
                return@launch
            }

            initDeviceUseCase()
                .onSuccess { checkStatus() }
                .onFailure { error ->
                    _uiState.value = SplashUiState.Error(
                        error.message
                            ?: "Gagal memuat aplikasi, pastikan anda terhubung internet.\nMengulang proses..."
                    )
                    delay(5000)
                    checkDeviceInit()
                }
        }
    }

    private suspend fun checkStatus() {
        try {
            val isConfirmMpinDone = appPreferences.getLoginExistingStatus()
            val isRegistDone = appPreferences.getRegistStatus()

            if (isConfirmMpinDone || isRegistDone) {
                delay(500)
                navigateToPortal()
            } else {
                delay(500)
                navigateToIntro()
            }
        } catch (e: Exception) {
            navigateToIntro()
        }
    }

    private suspend fun navigateToIntro() {
        if (navigated) return
        navigated = true
        _navigationEvent.send(SplashNavigationEvent.ToIntro)
    }

    private suspend fun navigateToPortal() {
        if (navigated) return
        navigated = true
        _navigationEvent.send(SplashNavigationEvent.ToPortal)
    }
}