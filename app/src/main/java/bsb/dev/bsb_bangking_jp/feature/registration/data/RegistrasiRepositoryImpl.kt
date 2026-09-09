package bsb.dev.bsb_bangking_jp.feature.registration.data

import bsb.dev.bsb_bangking_jp.core.crypto.JwtUtils
import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistrationRepository

private const val SUCCESS_CODE = "0000"

class RegistrationRepositoryImpl(
    private val api: RegistrationApiService,
    private val secureStorage: SecureStorageService,
) : RegistrationRepository {

    override suspend fun getAccount(atmCardNo: String, mobileNumber: String): Result<Unit> {
        return try {
            val response = api.getAccount(ApiHeaders.full(), GetAccountRequest(atmCardNo, mobileNumber))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Data akun tidak ditemukan.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<Unit> {
        return try {
            val response = api.resendOtp(ApiHeaders.full(), RegistResendOtpRequest(mobileNumber))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Gagal mengirim ulang OTP.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<String> {
        return try {
            val response = api.verifyOtp(ApiHeaders.full(), RegistVerifyOtpRequest(mobileNumber, otp))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "OTP tidak valid.")
                )
            }

            val challengeToken = body.data?.challengeToken
                ?: return Result.failure(ApiException("9999", "Challenge token tidak ditemukan."))

            Result.success(challengeToken)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun verifyDevice(challengeToken: String, mobileNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val challenge = JwtUtils.extractChallenge(challengeToken)
            val signature = SignatureUtils.signChallenge(challenge, privateKey)

            val headers = ApiHeaders.full() + mapOf(
                "Authorization" to "Bearer $challengeToken",
                "X-Signature" to signature,
            )

            val response = api.verifyDevice(headers, RegistVerifyDeviceRequest(mobileNumber = mobileNumber))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Verifikasi device gagal.")
                )
            }

            body.data?.accessToken?.let { secureStorage.saveRegistAccessToken(it) }
            body.data?.refreshToken?.let { secureStorage.saveRegistRefreshToken(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun addIdUser(
        mobileNumber: String,
        userId: String,
        confirmUserId: String,
    ): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = AddIdUserRequest(mobileNumber, userId, confirmUserId)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.addIdUser(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.REGIST),
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Gagal membuat ID Pengguna.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun addPasscode(
        mobileNumber: String,
        passcode: String,
        confirmPasscode: String,
    ): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = AddPasscodeRequest(mobileNumber, passcode, confirmPasscode)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.addPasscode(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.REGIST),
            )
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Gagal membuat kata sandi.")
                )
            }

            // 🔹 Padanan SecureStorageService.clearRegistTokens() di listener addPasscode Flutter.
            secureStorage.clearRegistTokens()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}