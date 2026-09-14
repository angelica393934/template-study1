package bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation

data class ForgetPwUiState(
    val atmCardNo: String = "",
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val atmCardError: String? = null,
    val phoneError: String? = null,
    val otpErrorMessage: String? = null,
    val newPasscodeError: String? = null,
)

sealed class ForgetPwNavEvent {
    object ToOtpPage : ForgetPwNavEvent()
    object ToResetPasswordPage : ForgetPwNavEvent()
    object ToPortalSuccess : ForgetPwNavEvent()
}

sealed class ForgetPwUiEvent {
    data class ShowToastError(val message: String) : ForgetPwUiEvent()
    object ShowOtpResentToast : ForgetPwUiEvent()
}