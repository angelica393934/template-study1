package bsb.dev.bsb_bangking_jp.feature.notification.presentation

import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem

sealed interface NotifUiState {
    data object Initial : NotifUiState
    data object Loading : NotifUiState
    data class Success(val items: List<NotifItem>) : NotifUiState
    data class Error(val message: String) : NotifUiState
}