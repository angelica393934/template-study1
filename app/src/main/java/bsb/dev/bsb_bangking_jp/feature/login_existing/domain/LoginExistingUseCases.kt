package bsb.dev.bsb_bangking_jp.feature.login_existing.domain

class LoginInitUseCase(private val repository: LoginExistingRepository) {
    suspend operator fun invoke(identifier: String): Result<Unit> = repository.loginInit(identifier)
}

class VerifyOtpUseCase(private val repository: LoginExistingRepository) {
    suspend operator fun invoke(identifier: String, otp: String): Result<String> =
        repository.verifyOtp(identifier, otp)
}

class ResendOtpUseCase(private val repository: LoginExistingRepository) {
    suspend operator fun invoke(identifier: String): Result<Unit> = repository.resendOtp(identifier)
}

class VerifyDeviceUseCase(private val repository: LoginExistingRepository) {
    suspend operator fun invoke(challengeToken: String, phoneNumber: String): Result<Unit> =
        repository.verifyDevice(challengeToken, phoneNumber)
}

class ConfirmMpinUseCase(private val repository: LoginExistingRepository) {
    suspend operator fun invoke(phoneNumber: String, confirmMpin: String): Result<Unit> =
        repository.confirmMpin(phoneNumber, confirmMpin)
}