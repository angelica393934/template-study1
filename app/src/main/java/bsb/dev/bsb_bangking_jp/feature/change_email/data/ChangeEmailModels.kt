package bsb.dev.bsb_bangking_jp.feature.change_email.data

import com.google.gson.annotations.SerializedName

// ===== 1) PUT changeemail =====
data class ChangeEmailRequest(
    @SerializedName("new_email") val newEmail: String,
)

data class ChangeEmailResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) POST confirmchangeemail =====
data class ConfirmChangeEmailRequest(
    @SerializedName("mobilepin") val mobilePin: String,
)

data class ConfirmChangeEmailResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)