package bsb.dev.bsb_bangking_jp.feature.ganti_email.data

import com.google.gson.annotations.SerializedName

// ===== 1) PUT changeemail =====
data class GantiEmailRequest(
    @SerializedName("new_email") val newEmail: String,
)

data class GantiEmailResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) POST confirmchangeemail =====
data class ConfirmGantiEmailRequest(
    @SerializedName("mobilepin") val mobilePin: String,
)

data class ConfirmGantiEmailResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)