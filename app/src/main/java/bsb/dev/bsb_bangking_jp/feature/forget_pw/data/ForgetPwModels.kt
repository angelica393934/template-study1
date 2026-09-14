package bsb.dev.bsb_bangking_jp.feature.forget_pw.data

import com.google.gson.annotations.SerializedName

// ===== 1) getpasscode =====
data class GetPwRequest(
    @SerializedName("atmcardno") val atmCardNo: String,
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class GetPwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) verify-otp-passcode =====
data class ForgetPwVerifyOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("otp") val otp: String,
)

data class ForgetPwVerifyOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ForgetPwTokenData? = null,
)

data class ForgetPwTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)

// ===== 3) dashboard/changepasscode =====
data class ChangePwRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("new_passcode") val newPasscode: String,
    @SerializedName("confirm_passcode") val confirmPasscode: String,
)

data class ChangePwResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 4) resend-otp/forgetpasscode (TANPA auth header) =====
data class ForgetPwResendOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class ForgetPwResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)