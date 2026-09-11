package bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain

interface ForgetIdUserRepository {
    suspend fun getIdUser(atmCardNo: String, mobileNumber: String): Result<Unit>
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit>
    suspend fun resendOtp(mobileNumber: String): Result<Unit>
    suspend fun changeIdUser(mobileNumber: String, newUserId: String, confirmUserId: String): Result<Unit>
}