package bsb.dev.bsb_bangking_jp.feature.registration.presentation

data class RegistrationUiState(
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val atmCardError: String? = null,
    val phoneInlineError: String? = null,
    val otpErrorMessage: String? = null,
    val userIdError: String? = null,
    val passcodeError: String? = null,
)

sealed class RegistrationNavEvent {
    object ToOtpPage : RegistrationNavEvent()
    object ToBuatIdPage : RegistrationNavEvent()
    object ToBuatPasswordPage : RegistrationNavEvent()
    object ToPortal : RegistrationNavEvent()
}

sealed class RegistrationUiEvent {
    data class ShowToastError(val message: String) : RegistrationUiEvent()
    object ShowOtpResentToast : RegistrationUiEvent()
}