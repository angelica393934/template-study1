// feature/beranda/data/RekeningLainnyaRepositoryImpl.kt
package bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.core.session.ClearableRepository
import bsb.dev.bsb_bangking_jp.core.util.retry
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.domain.RekeningLainnyaRepository

private const val SUCCESS_CODE = "0000"

class RekeningLainnyaRepositoryImpl(
    private val api: RekeningLainnyaApiService,
    private val secureStorage: SecureStorageService,
) : RekeningLainnyaRepository, ClearableRepository {

    private var cache: List<RekeningItem>? = null

    override val hasData: Boolean get() = cache != null
    override val cachedData: List<RekeningItem>? get() = cache

    override suspend fun getRekeningLainnya(forceRefresh: Boolean): List<RekeningItem> {
        if (!forceRefresh && cache != null) {
            return cache!!
        }
        val fresh = retry { fetchAccountSourceProfile() }
        cache = fresh
        return fresh
    }

    /** Padanan SetPrimaryAccountServices.primaryaccount -- PUT, respCode "0000" = sukses, X-Signature default. */
    override suspend fun setPrimaryAccount(accountNumber: String): Result<Unit> {
        return try {
            val privateKey = secureStorage.getPrivateKey()
                ?: return Result.failure(IllegalStateException("Private key tidak ditemukan."))

            val timestamp = ApiHeaders.currentTimestamp()
            val body = SetPrimaryAccountRequest(accountNumber = accountNumber)
            val signature = SignatureUtils.sign(body, timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.setPrimaryAccount(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            val respBody = response.body()
            val respCode = respBody?.respCode ?: "9999"
            val respMessage = respBody?.respMessage ?: "Terjadi kesalahan"

            if (!response.isSuccessful || respCode != SUCCESS_CODE) {
                return Result.failure(ApiException(respCode, respMessage))
            }

            // 🔹 Invalidate cache -- paksa fetch ulang biar isPrimary yang baru ke-refresh
            cache = null

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ApiException("9999", e.message ?: "Jaringan bermasalah. Silahkan coba lagi."))
        }
    }

    private suspend fun fetchAccountSourceProfile(): List<RekeningItem> {
        try {
            val privateKey = secureStorage.getPrivateKey()
                ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")

            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getAccountSourceProfile(
                headers = headers,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                throw ApiErrorParser.parse(response)
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                throw ApiException(body?.respCode ?: "-", body?.respMessage ?: "Gagal load rekening lainnya Periksa koneksi internet Anda dan coba lagi.")
            }

            return body.data
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(null, NetworkErrorMapper.toUserMessage(e))
        }
    }

    override fun clear() {
        cache = null
    }
}