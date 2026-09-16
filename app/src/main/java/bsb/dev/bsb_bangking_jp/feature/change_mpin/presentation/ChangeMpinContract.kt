package bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation

data class ChangeMpinUiState(
    val mobileNumber: String = "",
    val isLoading: Boolean = false,
    val oldMpinError: String? = null,
    val confirmMpinError: String? = null,
    val otpErrorMessage: String? = null,
)

sealed class ChangeMpinNavEvent {
    object ToNewPinPage : ChangeMpinNavEvent()
    object ToOtpPage : ChangeMpinNavEvent()
    object ToPortalSuccess : ChangeMpinNavEvent()
}