package bsb.dev.bsb_bangking_jp.feature.change_pw.data

import com.google.gson.annotations.SerializedName

// ===== 1) inputoldpassword =====
data class ValidateOldPwRequest(
    @SerializedName("passcode") val passcode: String,
)

data class ValidateOldPwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) changepassword =====
data class ChangePwRequest(
    @SerializedName("new_passcode") val newPasscode: String,
    @SerializedName("confirm_passcode") val confirmPasscode: String,
)

data class ChangePwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ChangePwData? = null,
)

// TODO: verifikasi ke backend -- key "mobileNumber" (camelCase) di sini BEDA dari
// pola umum project (biasanya "mobilenumber" lowercase), tapi ini persis mengikuti
// akses `state.changePwData!["data"]["mobileNumber"]` di GantiPwBloc/UbahKataSandiBaruPage.dart.
data class ChangePwData(
    @SerializedName("mobileNumber") val mobileNumber: String? = null,
)

// ===== 3) verify-otp-changepassword =====
data class VerifyOtpChangePwRequest(
    @SerializedName("otp") val otp: String,
)

data class VerifyOtpChangePwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 4) resend-otp-change/changepasscode =====
data class ResendOtpChangePwRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class ResendOtpChangePwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)