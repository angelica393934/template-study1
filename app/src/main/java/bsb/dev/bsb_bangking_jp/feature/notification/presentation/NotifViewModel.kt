package bsb.dev.bsb_bangking_jp.feature.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotifViewModel(
    private val repository: NotifRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotifUiState>(NotifUiState.Initial)
    val uiState: StateFlow<NotifUiState> = _uiState.asStateFlow()

    fun load(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { NotifUiState.Loading }
            try {
                val items = repository.getNotif(forceRefresh)
                _uiState.update { NotifUiState.Success(items) }
            } catch (e: Exception) {
                val message = (e as? ApiException)?.respMessage ?: e.message ?: "Gagal memuat notifikasi."
                _uiState.update { NotifUiState.Error(message) }
            }
        }
    }
}