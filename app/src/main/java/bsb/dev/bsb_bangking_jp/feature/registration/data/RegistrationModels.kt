package bsb.dev.bsb_bangking_jp.feature.registration.data

import com.google.gson.annotations.SerializedName

// ===== 1) getaccount =====
data class GetAccountRequest(
    @SerializedName("atmcardno") val atmCardNo: String,
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class GetAccountResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) resend-otp =====
data class RegistResendOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class RegistResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 3) verify-otp-registration =====
data class RegistVerifyOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("otp") val otp: String,
)

data class RegistVerifyOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: RegistVerifyOtpData? = null,
)

data class RegistVerifyOtpData(
    @SerializedName("challenge_token") val challengeToken: String? = null,
)

// ===== 4) verify-device =====
data class RegistVerifyDeviceRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class RegistVerifyDeviceResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: RegistVerifyDeviceData? = null,
)

data class RegistVerifyDeviceData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)

// ===== 5) v1/activation/addiduserlogin =====
data class AddIdUserRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("useridlogin") val userId: String,
    @SerializedName("confirm_useridlogin") val confirmUserId: String,
)

data class AddIdUserResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 6) v1/activation/addpasscode =====
data class AddPasscodeRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("passcode") val passcode: String,
    @SerializedName("confirm_passcode") val confirmPasscode: String,
)

data class AddPasscodeResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)