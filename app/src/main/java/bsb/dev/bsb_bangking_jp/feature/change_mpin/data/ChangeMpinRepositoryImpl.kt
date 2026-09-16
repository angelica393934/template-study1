package bsb.dev.bsb_bangking_jp.feature.change_mpin.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ChangeMpinRepository

private const val SUCCESS_CODE = "0000"

class ChangeMpinRepositoryImpl(
    private val api: ChangeMpinApiService,
    private val secureStorage: SecureStorageService,
) : ChangeMpinRepository {

    override suspend fun validateOldMpin(oldMpin: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ValidateOldMpinRequest(mobilePin = oldMpin)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.validateOldMpin(
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
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Validasi M-PIN lama gagal.")
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun changeMpin(newMpin: String, confirmMpin: String): Result<String> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = ChangeMpinRequest(newMpin = newMpin, confirmMpin = confirmMpin)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.changeMpin(
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
                    ApiException(respBody?.respCode, respBody?.respMessage ?: "Gagal mengubah M-PIN.")
                )
            }

            val mobileNumber = respBody.data?.mobileNumber
                ?: return Result.failure(ApiException("9999", "Nomor HP tidak ditemukan."))

            Result.success(mobileNumber)
        } catch (e: Exception) {
            Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
        }
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan, device belum ter-init."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = VerifyOtpChangeMpinRequest(mobileNumber = mobileNumber, otp = otp)
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
}