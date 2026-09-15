package bsb.dev.bsb_bangking_jp.feature.change_pw.domain

interface ChangePwRepository {
    /** Padanan GantiPwService.inputOldMpin() -- POST /v1/dashboard/inputoldpassword. */
    suspend fun validateOldPw(oldPasscode: String): Result<Unit>

    /** Sukses -> mengembalikan mobileNumber (dipakai lanjut ke halaman OTP). */
    suspend fun changePw(newPasscode: String, confirmPasscode: String): Result<String>

    /** Padanan GantiPwService.verifyOtp() -- POST /v1/dashboard/verify-otp-changepassword. */
    suspend fun verifyOtp(otp: String): Result<Unit>

    /** Padanan GantiPwService.ResendOtp() -- POST /v1/resend-otp-change/changepasscode. */
    suspend fun resendOtp(mobileNumber: String): Result<Unit>
}