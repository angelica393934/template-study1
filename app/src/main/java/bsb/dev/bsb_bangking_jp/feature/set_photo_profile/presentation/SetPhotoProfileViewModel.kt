package bsb.dev.bsb_bangking_jp.feature.set_photo_profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.feature.set_photo_profile.domain.SetPhotoProfileRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class PhotoProfileSuccessAction { UPDATE, DELETE }

data class PhotoProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class PhotoProfileEvent {
    data class Success(val action: PhotoProfileSuccessAction) : PhotoProfileEvent()
}

class PhotoProfileViewModel(
    private val repository: SetPhotoProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoProfileUiState())
    val uiState: StateFlow<PhotoProfileUiState> = _uiState.asStateFlow()

    private val _event = Channel<PhotoProfileEvent>(Channel.BUFFERED)
    val event: Flow<PhotoProfileEvent> = _event.receiveAsFlow()

    fun updatePhoto(imageFile: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.updatePhotoProfile(imageFile)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _event.send(PhotoProfileEvent.Success(PhotoProfileSuccessAction.UPDATE))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = (error as? ApiException)?.respMessage ?: error.message,
                        )
                    }
                }
        }
    }

    fun deletePhoto(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.deletePhotoProfile(userId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _event.send(PhotoProfileEvent.Success(PhotoProfileSuccessAction.DELETE))
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = (error as? ApiException)?.respMessage ?: error.message,
                        )
                    }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}