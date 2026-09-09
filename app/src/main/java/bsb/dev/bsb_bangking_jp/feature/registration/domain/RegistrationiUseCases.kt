package bsb.dev.bsb_bangking_jp.feature.registration.domain

class GetAccountUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(atmCardNo: String, mobileNumber: String): Result<Unit> =
        repository.getAccount(atmCardNo, mobileNumber)
}

class RegistResendOtpUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<Unit> =
        repository.resendOtp(mobileNumber)
}

class RegistVerifyOtpUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<String> =
        repository.verifyOtp(mobileNumber, otp)
}

class RegistVerifyDeviceUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(challengeToken: String, mobileNumber: String): Result<Unit> =
        repository.verifyDevice(challengeToken, mobileNumber)
}

class AddIdUserUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(mobileNumber: String, userId: String, confirmUserId: String): Result<Unit> =
        repository.addIdUser(mobileNumber, userId, confirmUserId)
}

class AddPasscodeUseCase(private val repository: RegistrationRepository) {
    suspend operator fun invoke(mobileNumber: String, passcode: String, confirmPasscode: String): Result<Unit> =
        repository.addPasscode(mobileNumber, passcode, confirmPasscode)
}