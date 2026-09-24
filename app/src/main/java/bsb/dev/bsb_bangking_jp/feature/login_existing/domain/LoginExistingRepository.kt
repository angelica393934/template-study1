// feature/login_existing/domain/LoginRepository.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing.domain

interface LoginExistingRepository {
    suspend fun loginInit(identifier: String): Result<Unit>
    /** Sukses -> mengembalikan challenge_token (dipakai lanjut ke verifyDevice). */
    suspend fun verifyOtp(identifier: String, otp: String): Result<String>
    suspend fun resendOtp(identifier: String): Result<Unit>
    suspend fun verifyDevice(challengeToken: String, phoneNumber: String): Result<Unit>
    suspend fun confirmMpin(phoneNumber: String, confirmMpin: String): Result<Unit>
}