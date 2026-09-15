package bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation

data class GantiEmailUiState(
    val newEmail: String = "",
    val isLoading: Boolean = false,
    val emailInlineError: String? = null,
)

sealed class GantiEmailNavEvent {
    object ToPinPage : GantiEmailNavEvent()
}

sealed class GantiEmailUiEvent {
    data class ShowToastError(val message: String) : GantiEmailUiEvent()
}