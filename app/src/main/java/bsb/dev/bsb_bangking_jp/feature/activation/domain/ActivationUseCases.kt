package bsb.dev.bsb_bangking_jp.feature.activation.domain

class GetAccountActivationUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(atmCardNo: String): Result<Unit> =
        repository.getAccountActivation(atmCardNo)
}

class ValidateUserIdUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(userId: String): Result<String> =
        repository.validateUserId(userId)
}

class ActivationResendOtpUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<Unit> =
        repository.resendOtp(mobileNumber)
}

class ActivationVerifyOtpUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<Unit> =
        repository.verifyOtp(mobileNumber, otp)
}

class ValidatePasscodeUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(passcode: String): Result<Unit> =
        repository.validatePasscode(passcode)
}

class ConfirmMpinActivationUseCase(private val repository: ActivationRepository) {
    suspend operator fun invoke(confirmMpin: String): Result<Unit> =
        repository.confirmMpin(confirmMpin)
}