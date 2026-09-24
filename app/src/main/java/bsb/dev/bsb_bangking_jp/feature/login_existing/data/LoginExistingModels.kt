package bsb.dev.bsb_bangking_jp.feature.login_existing.data

import com.google.gson.annotations.SerializedName

// 1. login init
data class LoginInitRequest(
    @SerializedName("identifier") val identifier: String,
)

data class LoginInitResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

// 2. verify otp
data class VerifyOtpRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("otp") val otp: String,
)

data class VerifyOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: VerifyOtpData? = null,
)

data class VerifyOtpData(
    @SerializedName("challenge_token") val challengeToken: String? = null,
)

// 3. resend otp
data class ResendOtpRequest(@SerializedName("mobilenumber") val identifier: String)

data class ResendOtpResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)

//4. verify device
data class VerifyDeviceRequest(@SerializedName("identifier") val identifier: String)

data class VerifyDeviceResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
    @SerializedName("data") val data: VerifyDeviceData? = null,
)

data class VerifyDeviceData(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
)

//5. confirm mpin
data class ConfirmMpinRequest(
    @SerializedName("mobilenumber") val mobileNumber: String,
    @SerializedName("confirm_mpin") val confirmMpin: String,
)

data class ConfirmMpinResponse(
    @SerializedName("respCode") val respCode: String? = null,
    @SerializedName("respMessage") val respMessage: String? = null,
)