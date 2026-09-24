package bsb.dev.bsb_bangking_jp.shared.profile.presentation

import bsb.dev.bsb_bangking_jp.shared.profile.data.ProfileData

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfileData? = null,
    val error: String? = null,
    val isPhotoLoading: Boolean = false,
    val photoBytes: ByteArray? = null,
)