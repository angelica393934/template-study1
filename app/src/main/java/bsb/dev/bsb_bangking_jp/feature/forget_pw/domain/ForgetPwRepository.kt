package bsb.dev.bsb_bangking_jp.feature.forget_pw.domain

interface ForgetPwRepository {
    suspend fun getPw(atmCardNo: String, mobileNumber: String): Result<Unit>
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit>
    suspend fun resendOtp(mobileNumber: String): Result<Unit>
    suspend fun changePw(mobileNumber: String, newPasscode: String, confirmPasscode: String): Result<Unit>
}