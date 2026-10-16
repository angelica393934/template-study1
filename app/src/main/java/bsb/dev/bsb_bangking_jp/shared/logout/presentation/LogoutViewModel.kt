package bsb.dev.bsb_bangking_jp.shared.logout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.shared.logout.domain.LogoutRepository
import bsb.dev.bsb_bangking_jp.shared.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogoutViewModel(
    private val repository: LogoutRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LogoutUiState>(LogoutUiState.Initial)
    val uiState: StateFlow<LogoutUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            _uiState.update { LogoutUiState.Loading }

            repository.logout()
                .onSuccess {
                    // 🔥 padanan sessionManager.clearSession() di listener sukses LogoutBloc:
                    // hapus token + bersihkan cache semua repository.
                    sessionManager.clearSession()
                    _uiState.update { LogoutUiState.Success }
                }
                .onFailure { error ->
                    val respCode = (error as? ApiException)?.respCode ?: "9999"
                    val message = error.message ?: "Logout gagal, silakan coba lagi."
                    _uiState.update { LogoutUiState.Failure(respCode, message) }
                }
        }
    }

    /** Reset ke Initial supaya sheet bisa dibuka ulang bersih tanpa bawa state lama. */
    fun resetState() {
        _uiState.update { LogoutUiState.Initial }
    }
}