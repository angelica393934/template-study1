package bsb.dev.bsb_bangking_jp.feature.change_email.presentation

data class ChangeEmailUiState(
    val newEmail: String = "",
    val isLoading: Boolean = false,
    val emailInlineError: String? = null,
)

sealed class ChangeEmailNavEvent {
    object ToPinPage : ChangeEmailNavEvent()
}

sealed class ChangeEmailUiEvent {
    data class ShowToastError(val message: String) : ChangeEmailUiEvent()
}