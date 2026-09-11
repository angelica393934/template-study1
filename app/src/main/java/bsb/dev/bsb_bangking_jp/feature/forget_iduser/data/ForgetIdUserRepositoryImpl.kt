package bsb.dev.bsb_bangking_jp.feature.forget_iduser.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdUserRepository

private const val SUCCESS_CODE = "0000"

class ForgetIdUserRepositoryImpl(
    private val api: ForgetIdUserApiService,
    private val secureStorage: SecureStorageService,
) : ForgetIdUserRepository {

    override suspend fun getIdUser(atmCardNo: String, mobileNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = GetIdUserRequest(atmCardNo = atmCardNo, mobileNumber = mobileNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getIdUser(
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
            val body = ForgetIdVerifyOtpRequest(mobileNumber = mobileNumber, otp = otp)
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

            // 🔹 Padanan penyimpanan access_token/refresh_token forget_iduser di LupaIdUserService.verifyOtp.
            respBody.data?.accessToken?.let { secureStorage.saveForgetIdUserAccessToken(it) }
            respBody.data?.refreshToken?.let { secureStorage.saveForgetIdUserRefreshToken(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<Unit> {
        return try {
            val response = api.resendOtp(ApiHeaders.full(), ForgetIdResendOtpRequest(mobileNumber))
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

    override suspend fun changeIdUser(
        mobileNumber: String,
        newUserId: String,
        confirmUserId: String,
    ): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ChangeIdUserRequest(
                mobileNumber = mobileNumber,
                newUserId = newUserId,
                confirmUserId = confirmUserId,
            )
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.changeIdUser(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.FORGET_ID_USER),
            )
            if (!response.isSuccessful) return Result.failure(ApiErrorParser.parse(response))

            val respBody = response.body()
            if (respBody?.respCode != SUCCESS_CODE) {
                return Result.failure(
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Failed to update User ID.")
                )
            }

            // 🔹 Token forget_iduser tidak dipakai lagi setelah siklus ini selesai.
            secureStorage.clearForgetIdUserTokens()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }
}