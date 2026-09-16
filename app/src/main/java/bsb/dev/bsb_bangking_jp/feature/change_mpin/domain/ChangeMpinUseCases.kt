package bsb.dev.bsb_bangking_jp.feature.change_mpin.domain

class ValidateOldMpinUseCase(private val repository: ChangeMpinRepository) {
    suspend operator fun invoke(oldMpin: String): Result<Unit> = repository.validateOldMpin(oldMpin)
}

class ChangeMpinUseCase(private val repository: ChangeMpinRepository) {
    suspend operator fun invoke(newMpin: String, confirmMpin: String): Result<String> =
        repository.changeMpin(newMpin, confirmMpin)
}

class VerifyOtpChangeMpinUseCase(private val repository: ChangeMpinRepository) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<Unit> =
        repository.verifyOtp(mobileNumber, otp)
}