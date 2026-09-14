package bsb.dev.bsb_bangking_jp.feature.forget_pw.domain

class GetPwUseCase(private val repository: ForgetPwRepository) {
    suspend operator fun invoke(atmCardNo: String, mobileNumber: String): Result<Unit> =
        repository.getPw(atmCardNo, mobileNumber)
}

class ForgetPwVerifyOtpUseCase(private val repository: ForgetPwRepository) {
    suspend operator fun invoke(mobileNumber: String, otp: String): Result<Unit> =
        repository.verifyOtp(mobileNumber, otp)
}

class ForgetPwResendOtpUseCase(private val repository: ForgetPwRepository) {
    suspend operator fun invoke(mobileNumber: String): Result<Unit> =
        repository.resendOtp(mobileNumber)
}

class ChangePwUseCase(private val repository: ForgetPwRepository) {
    suspend operator fun invoke(mobileNumber: String, newPasscode: String, confirmPasscode: String): Result<Unit> =
        repository.changePw(mobileNumber, newPasscode, confirmPasscode)
}