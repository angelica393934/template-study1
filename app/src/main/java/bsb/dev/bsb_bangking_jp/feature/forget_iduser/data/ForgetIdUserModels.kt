package bsb.dev.bsb_bangking_jp.feature.forget_iduser.data

import com.google.gson.annotations.SerializedName

// ===== 1) getiduser =====
data class GetIdUserRequest(
    @SerializedName("atmcardno") val atmCardNo: String,
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class GetIdUserResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 2) verify-otp-iduser =====
data class ForgetIdVerifyOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("otp") val otp: String,
)

data class ForgetIdVerifyOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: ForgetIdUserTokenData? = null,
)

data class ForgetIdUserTokenData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)

// ===== 3) dashboard/changeiduser =====
data class ChangeIdUserRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("new_user_id") val newUserId: String,
    @SerializedName("confirm_user_id") val confirmUserId: String,
)

data class ChangeIdUserResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// ===== 4) resend-otp/forgetuserid (TANPA auth header) =====
data class ForgetIdResendOtpRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
)

data class ForgetIdResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)