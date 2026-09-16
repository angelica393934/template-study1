package bsb.dev.bsb_bangking_jp.feature.change_mpin.domain

interface ChangeMpinRepository {
    /** Padanan GantiMpinService.inputOldMpin() -- POST /v1/dashboard/inputoldmpin. */
    suspend fun validateOldMpin(oldMpin: String): Result<Unit>

    /** Sukses -> mengembalikan mobileNumber (dipakai lanjut ke halaman OTP). */
    suspend fun changeMpin(newMpin: String, confirmMpin: String): Result<String>

    /** Padanan GantiMpinService.verifyOtp() -- POST /v1/dashboard/verify-otp-changempin. */
    suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit>
}