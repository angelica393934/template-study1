package bsb.dev.bsb_bangking_jp.feature.registration.domain

interface RegistrationRepository {
    suspend fun getAccount(atmCardNo: String, mobileNumber: String): Result<Unit>
    suspend fun resendOtp(mobileNumber: String): Result<Unit>
    /** Sukses -> mengembalikan challenge_token (dipakai lanjut ke verifyDevice). */
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<String>
    suspend fun verifyDevice(challengeToken: String, mobileNumber: String): Result<Unit>
    suspend fun addIdUser(mobileNumber: String, userId: String, confirmUserId: String): Result<Unit>
    suspend fun addPasscode(mobileNumber: String, passcode: String, confirmPasscode: String): Result<Unit>
}