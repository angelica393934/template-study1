package bsb.dev.bsb_bangking_jp.feature.activation.presentation

data class ActivationUiState(
    val atmCardNo: String = "",
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val atmCardError: String? = null,
    val userIdError: String? = null,
    val otpErrorMessage: String? = null,
    val passcodeError: String? = null,
    val confirmPinError: String? = null,
)

sealed class ActivationNavEvent {
    object ToIdPage : ActivationNavEvent()
    object ToOtpPage : ActivationNavEvent()
    object ToPasswordPage : ActivationNavEvent()
    object ToPinPage : ActivationNavEvent()
    object ToPortal : ActivationNavEvent()
}

sealed class ActivationUiEvent {
    data class ShowToastError(val message: String) : ActivationUiEvent()
    object ShowOtpResentToast : ActivationUiEvent()
}