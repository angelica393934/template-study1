package bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain

class GetIdUserUseCase(private val repository: ForgetIdUserRepository) {
    suspend operator fun invoke(atmCardNo: String, mobileNumber: String): Result<Unit> =
        repository.getIdUser(atmCardNo, mobileNumber)
}

class ForgetIdVerifyOtpUseCase(private val repository: ForgetIdUserRepository) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<Unit> =
        repository.verifyOtp(mobileNumber, otp)
}

class ForgetIdResendOtpUseCase(private val repository: ForgetIdUserRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<Unit> =
        repository.resendOtp(mobileNumber)
}

class ChangeIdUserUseCase(private val repository: ForgetIdUserRepository) {
    suspend operator fun invoke(mobileNumber: String, newUserId: String, confirmUserId: String): Result<Unit> =
        repository.changeIdUser(mobileNumber, newUserId, confirmUserId)
}