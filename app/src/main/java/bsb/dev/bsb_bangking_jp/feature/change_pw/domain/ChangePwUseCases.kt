package bsb.dev.bsb_bangking_jp.feature.change_pw.domain

class ValidateOldPwUseCase(private val repository: ChangePwRepository) {
    suspend operator fun invoke(oldPasscode: String): Result<Unit> =
        repository.validateOldPw(oldPasscode)
}

class ChangePwUseCase(private val repository: ChangePwRepository) {
    suspend operator fun invoke(newPasscode: String, confirmPasscode: String): Result<String> =
        repository.changePw(newPasscode, confirmPasscode)
}

class VerifyOtpChangePwUseCase(private val repository: ChangePwRepository) {
    suspend operator fun invoke(otp: String): Result<Unit> = repository.verifyOtp(otp)
}

class ResendOtpChangePwUseCase(private val repository: ChangePwRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<Unit> = repository.resendOtp(mobileNumber)
}