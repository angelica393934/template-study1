package bsb.dev.bsb_bangking_jp.feature.change_pw.presentation

data class ChangePwUiState(
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val oldPasscodeError: String? = null,
    val newPasscodeError: String? = null,
    val otpErrorMessage: String? = null,
)

sealed class ChangePwNavEvent {
    object ToNewPasswordPage : ChangePwNavEvent()
    object ToOtpPage : ChangePwNavEvent()
    object ToPortalSuccess : ChangePwNavEvent()
}

sealed class ChangePwUiEvent {
    data class ShowToastError(val message: String) : ChangePwUiEvent()
    object ShowOtpResentToast : ChangePwUiEvent()
}