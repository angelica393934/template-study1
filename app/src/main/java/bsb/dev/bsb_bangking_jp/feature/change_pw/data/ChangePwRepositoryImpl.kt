package bsb.dev.bsb_bangking_jp.feature.change_pw.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ChangePwRepository

private const val SUCCESS_CODE = "0000"

class ChangePwRepositoryImpl(
    private val api: ChangePwApiService,
    private val secureStorage: SecureStorageService,
) : ChangePwRepository {

    override suspend fun validateOldPw(oldPasscode: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ValidateOldPwRequest(passcode = oldPasscode)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.validateOldPw(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Validasi kata sandi lama gagal.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun changePw(newPasscode: String, confirmPasscode: String): Result<String> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ChangePwRequest(newPasscode = newPasscode, confirmPasscode = confirmPasscode)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.changePw(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Gagal mengubah kata sandi.")
                )
            }

            val mobileNumber = respBody.data?.mobileNumber
                ?: return Result.failure(ApiException("9999", "Nomor HP tidak ditemukan."))

            Result.success(mobileNumber)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun verifyOtp(otp: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = VerifyOtpChangePwRequest(otp = otp)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.verifyOtp(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Verifikasi OTP gagal.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ResendOtpChangePwRequest(mobileNumber = mobileNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.resendOtp(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Gagal mengirim ulang OTP.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}