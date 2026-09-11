package bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation

data class ForgetIdUserUiState(
    val atmCardNo: String = "",
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val atmCardError: String? = null,
    val phoneError: String? = null,
    val otpErrorMessage: String? = null,
    val newUserIdError: String? = null,
)

sealed class ForgetIdUserNavEvent {
    object ToOtpPage : ForgetIdUserNavEvent()
    object ToResetIdPage : ForgetIdUserNavEvent()
    object ToPortalSuccess : ForgetIdUserNavEvent()
}

sealed class ForgetIdUserUiEvent {
    data class ShowToastError(val message: String) : ForgetIdUserUiEvent()
    object ShowOtpResentToast : ForgetIdUserUiEvent()
}