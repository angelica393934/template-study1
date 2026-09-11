// feature/login_existing/data/LoginRepositoryImpl.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing.data

import bsb.dev.bsb_bangking_jp.core.crypto.JwtUtils
import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.AppPreferences
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.LoginRepository

private const val SUCCESS_CODE = "0000"

class LoginRepositoryImpl(
    private val api: LoginApiService,
    private val secureStorage: SecureStorageService,
    private val appPreferences: AppPreferences,
) : LoginRepository {

    override suspend fun loginInit(identifier: String): Result<Unit> {
        return try {
            val response = api.loginInit(ApiHeaders.full(), LoginInitRequest(identifier))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Gagal memproses nomor HP.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun verifyOtp(identifier: String, otp: String): Result<String> {
        return try {
            val response = api.verifyOtp(ApiHeaders.full(), VerifyOtpRequest(identifier, otp))
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

    override suspend fun resendOtp(identifier: String): Result<Unit> {
        return try {
            val response = api.resendOtp(ApiHeaders.full(), ResendOtpRequest(identifier))
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

    override suspend fun verifyDevice(challengeToken: String, phoneNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val challenge = JwtUtils.extractChallenge(challengeToken)
            val signature = SignatureUtils.signChallenge(challenge, privateKey)

            val headers = ApiHeaders.full() + mapOf(
                "Authorization" to "Bearer $challengeToken",
                "X-Signature" to signature,
            )

            val response = api.verifyDevice(headers, VerifyDeviceRequest(identifier = phoneNumber))
            if (!response.isSuccessful) {
                return Result.failure(ApiErrorParser.parse(response))
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(body?.respCode, body?.respMessage ?: "Verifikasi device gagal.")
                )
            }

            body.data?.accessToken?.let { secureStorage.saveInitAccessToken(it) }
            body.data?.refreshToken?.let { secureStorage.saveInitRefreshToken(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun confirmMpin(phoneNumber: String, confirmMpin: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ConfirmMpinRequest(mobileNumber = phoneNumber, confirmMpin = confirmMpin)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.confirmMpin(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.INIT),
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

            secureStorage.clearInitTokens()
            appPreferences.saveLoginExistingStatus(true)
            appPreferences.saveLoginAllowed(true)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}