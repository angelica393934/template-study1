package bsb.dev.bsb_bangking_jp.feature.forget_pw.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwRepository

private const val SUCCESS_CODE = "0000"

class ForgetPwRepositoryImpl(
    private val api: ForgetPwApiService,
    private val secureStorage: SecureStorageService,
) : ForgetPwRepository {

    override suspend fun getPw(atmCardNo: String, mobileNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = GetPwRequest(atmCardNo = atmCardNo, mobileNumber = mobileNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getPw(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.INIT),
            )
            if (!response.isSuccessful) return Result.failure(ApiErrorParser.parse(response))

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Account data not found.")
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
            val body = ForgetPwVerifyOtpRequest(mobileNumber = mobileNumber, otp = otp)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.verifyOtp(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.INIT),
            )
            if (!response.isSuccessful) return Result.failure(ApiErrorParser.parse(response))

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Invalid OTP.")
                )
            }

            // 🔹 Padanan penyimpanan access_token/refresh_token forget_pw di LupaPwService.verifyOtp.
            respBody.data?.accessToken?.let { secureStorage.saveForgetPwUserAccessToken(it) }
            respBody.data?.refreshToken?.let { secureStorage.saveForgetPwUserRefreshToken(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<Unit> {
        return try {
            val response = api.resendOtp(ApiHeaders.full(), ForgetPwResendOtpRequest(mobileNumber))
            if (!response.isSuccessful) return Result.failure(ApiErrorParser.parse(response))

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Failed to resend OTP.")
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun changePw(
        mobileNumber: String,
        newPasscode: String,
        confirmPasscode: String,
    ): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ChangePwRequest(
                mobileNumber = mobileNumber,
                newPasscode = newPasscode,
                confirmPasscode = confirmPasscode,
            )
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.changePw(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.FORGET_PW_USER),
            )
            if (!response.isSuccessful) return Result.failure(ApiErrorParser.parse(response))

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Failed to update password.")
                )
            }

            // 🔹 Token forget_pw tidak dipakai lagi setelah siklus ini selesai.
            secureStorage.clearForgetPwTokens()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}