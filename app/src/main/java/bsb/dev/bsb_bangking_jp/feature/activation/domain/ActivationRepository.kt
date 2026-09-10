package bsb.dev.bsb_bangking_jp.feature.activation.domain

interface ActivationRepository {
    suspend fun getAccountActivation(atmCardNo: String): Result<Unit>
    /** Sukses -> mengembalikan mobileNumber (dipakai lanjut ke halaman OTP). */
    suspend fun validateUserId(userId: String): Result<String>
    suspend fun resendOtp(mobileNumber: String): Result<Unit>
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit>
    suspend fun validatePasscode(passcode: String): Result<Unit>
    suspend fun confirmMpin(confirmMpin: String): Result<Unit>
}