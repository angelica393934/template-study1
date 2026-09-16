package bsb.dev.bsb_bangking_jp.feature.change_mpin.data

import com.google.gson.annotations.SerializedName

// ===== 1) inputoldmpin =====
data class ValidateOldMpinRequest(
    @SerializedName("mobilepin") val mobilePin: String,
)

data class ValidateOldMpinResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) changempin =====
data class ChangeMpinRequest(
    @SerializedName("new_mpin") val newMpin: String,
    @SerializedName("confirm_mpin") val confirmMpin: String,
)

data class ChangeMpinResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ChangeMpinData? = null,
)

// TODO: verifikasi ke backend -- key "mobileNumber" (camelCase), mengikuti akses
// `msg["data"]?["mobileNumber"]` di GantiMpinBloc.dart persis.
data class ChangeMpinData(
    @SerializedName("mobileNumber") val mobileNumber: String? = null,
)

// ===== 3) verify-otp-changempin =====
data class VerifyOtpChangeMpinRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("otp") val otp: String,
)

data class VerifyOtpChangeMpinResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)