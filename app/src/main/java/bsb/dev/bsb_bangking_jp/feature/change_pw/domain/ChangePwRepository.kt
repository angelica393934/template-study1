package bsb.dev.bsb_bangking_jp.feature.change_pw.domain

interface ChangePwRepository {
    suspend fun validateOldPw(oldPasscode: String): Result<Unit>
    suspend fun changePw(newPasscode: String, confirmPasscode: String): Result<String>
    suspend fun verifyOtpChangePw(otp: String): Result<Unit>
    suspend fun resendOtpChangePw(mobileNumber: String): Result<Unit>
}