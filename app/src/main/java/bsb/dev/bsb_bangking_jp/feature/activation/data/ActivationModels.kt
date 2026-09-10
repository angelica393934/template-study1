package bsb.dev.bsb_bangking_jp.feature.activation.data

import com.google.gson.annotations.SerializedName

// ===== 1) getaccountactivation =====
data class GetAccountActivationRequest(
    @SerializedName("atmcardno") val atmCardNo: String,
)

data class GetAccountActivationResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) validationuseridforactivation =====
data class ValidateUserIdRequest(
    @SerializedName("useridlogin") val userId: String,
)

data class ValidateUserIdResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ValidateUserIdData? = null,
)

// TODO: verifikasi ke backend -- key "mobileNumber" (camelCase) BERBEDA dari
// endpoint lain di project ini yang umumnya pakai "mobilenumber", tapi ini
// persis mengikuti source Flutter aslinya (data["data"]?["mobileNumber"]).
data class ValidateUserIdData(
    @SerializedName("mobileNumber") val mobileNumber: String? = null,
)

// ===== 3) resend-otp/validuseridactivation (TANPA auth header) =====
data class ActivationResendOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class ActivationResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 4) verify-otp-useridactivation =====
data class ActivationVerifyOtpRequest(
    @SerializedName("otp") val otp: String,
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class ActivationVerifyOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ActivationTokenData? = null,
)

data class ActivationTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)

// ===== 5) validationpasscodeforactivation =====
data class ValidatePasscodeRequest(
    @SerializedName("passcode") val passcode: String,
)

data class ValidatePasscodeResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 6) activation-confirmmpin =====
data class ActivationConfirmMpinRequest(
    @SerializedName("confirm_mpin") val confirmMpin: String,
)

data class ActivationConfirmMpinResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)