package bsb.dev.bsb_bangking_jp.feature.activation.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.AppPreferences
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationRepository

private const val SUCCESS_CODE = "0000"

class ActivationRepositoryImpl(
    private val api: ActivationApiService,
    private val secureStorage: SecureStorageService,
    private val appPreferences: AppPreferences,
) : ActivationRepository {

    override suspend fun getAccountActivation(atmCardNo: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))
            val timestamp = ApiHeaders.currentTimestamp()
            val body = GetAccountActivationRequest(atmCardNo)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getAccountActivation(
                headers = headers,
                body = body,
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }
            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Data akun tidak ditemukan.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    /** Sukses -> mengembalikan mobileNumber (dipakai lanjut ke halaman OTP). */
    override suspend fun validateUserId(userId: String): Result<String> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))
            val timestamp = ApiHeaders.currentTimestamp()
            val body = ValidateUserIdRequest(userId)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.validateUserId(
                headers = headers,
                body = body,
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "ID Pengguna tidak valid.")
                )
            }

            val mobileNumber = respBody.data?.mobileNumber
                ?: return Result.failure(ApiException("9999", "Nomor HP tidak ditemukan."))

            Result.success(mobileNumber)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))
            val timestamp = ApiHeaders.currentTimestamp()
            val body = ActivationResendOtpRequest(mobileNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.resendOtp(
                headers = headers,
                body = body,
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

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))
            val timestamp = ApiHeaders.currentTimestamp()
            val body = ActivationVerifyOtpRequest(otp = otp, mobileNumber = mobileNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))


            val response = api.verifyOtp(
                headers = headers,
                body = body,
                )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "OTP tidak valid.")
                )
            }

            // 🔹 Padanan penyimpanan access_token/refresh_token di ActivationBloc.verifyOtp.
            respBody.data?.accessToken?.let { secureStorage.saveActivationAccessToken(it) }
            respBody.data?.refreshToken?.let { secureStorage.saveActivationRefreshToken(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun validatePasscode(passcode: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ValidatePasscodeRequest(passcode)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.validatePasscode(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.ACTIVATION),
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Kata sandi tidak valid.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun confirmMpin(confirmMpin: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ActivationConfirmMpinRequest(confirmMpin)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.confirmMpin(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.ACTIVATION),
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Konfirmasi PIN gagal.")
                )
            }
            appPreferences.saveLoginAllowed(true)
            secureStorage.clearActivationTokens()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}