package bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.data

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
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferDetail
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferItem
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferRepository

private const val SUCCESS_CODE = "0000"

class ScheduledTransferRepositoryImpl(
    private val api: ScheduledTransferApiService,
    private val secureStorage: SecureStorageService,
) : ScheduledTransferRepository, ClearableRepository {

    // 🔹 Cache in-memory, padanan `_cachedList` & `_cachedDetail` di ScheduledTransferBloc (Dart).
    private var listCache: List<ScheduledTransferItem>? = null
    private val detailCache = mutableMapOf<Int, ScheduledTransferDetail>()

    override suspend fun getScheduledTransfers(forceRefresh: Boolean): List<ScheduledTransferItem> {
        if (!forceRefresh) {
            listCache?.let { return it }
        }
        val fresh = retry { fetchScheduledTransfers() }
        listCache = fresh
        return fresh
    }

    override suspend fun getScheduledTransferDetail(id: Int, forceRefresh: Boolean): ScheduledTransferDetail {
        if (!forceRefresh) {
            detailCache[id]?.let { return it }
        }
        val fresh = retry { fetchScheduledTransferDetail(id) }
        detailCache[id] = fresh
        return fresh
    }

    /** Padanan `togglePause` -- invalidate cache list & detail id terkait. */
    override suspend fun togglePause(id: Int): Result<Unit> = runCatchingApi {
        val body = ScheduledTransferIdRequest(id = id)
        val headers = signedHeaders(body)

        val response = api.togglePause(
            headers = headers,
            body = body,
            tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal memperbarui status transfer terjadwal.")
        }

        listCache = null
        detailCache.remove(id)
    }

    /** Padanan `deleteScheduledTransfer` -- invalidate cache list & semua detail id yang dihapus. */
    override suspend fun deleteScheduledTransfer(ids: List<Int>): Result<Unit> = runCatchingApi {
        val body = DeleteScheduledTransferRequest(id = ids)
        val headers = signedHeaders(body)

        val response = api.deleteScheduledTransfer(
            headers = headers,
            body = body,
            tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
        )
        if (!response.isSuccessful) throw ApiErrorParser.parse(response)

        val resp = response.body()
        if (resp?.respCode != SUCCESS_CODE) {
            throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal menghapus transfer terjadwal.")
        }

        listCache = null
        ids.forEach { detailCache.remove(it) }
    }

    private suspend fun fetchScheduledTransfers(): List<ScheduledTransferItem> {
        try {
            val timestamp = ApiHeaders.currentTimestamp()
            val privateKey = secureStorage.getPrivateKey()
                ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getScheduledTransfers(
                headers = headers,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )
            if (!response.isSuccessful) throw ApiErrorParser.parse(response)

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                throw ApiException(body?.respCode, body?.respMessage ?: "Gagal memuat daftar transfer terjadwal.")
            }

            return body.data.map { it.toDomain() }
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(null, NetworkErrorMapper.toUserMessage(e))
        }
    }

    private suspend fun fetchScheduledTransferDetail(id: Int): ScheduledTransferDetail {
        try {
            val body = ScheduledTransferIdRequest(id = id)
            val headers = signedHeaders(body)

            val response = api.getScheduledTransferDetail(
                headers = headers,
                body = body,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )
            if (!response.isSuccessful) throw ApiErrorParser.parse(response)

            val resp = response.body()
            if (resp?.respCode != SUCCESS_CODE) {
                throw ApiException(resp?.respCode, resp?.respMessage ?: "Gagal memuat detail transfer terjadwal.")
            }

            return resp.data?.toDomain()
                ?: throw ApiException("9999", "Data transfer terjadwal tidak ditemukan.")
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(null, NetworkErrorMapper.toUserMessage(e))
        }
    }

    private fun signedHeaders(body: Any): Map<String, String> {
        val privateKey = secureStorage.getPrivateKey()
            ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")
        val timestamp = ApiHeaders.currentTimestamp()
        val signature = SignatureUtils.sign(body, timestamp, privateKey)
        return ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))
    }

    private suspend fun <T> runCatchingApi(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: ApiException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(ApiException(null, NetworkErrorMapper.toUserMessage(e)))
    }

    override fun clear() {
        listCache = null
        detailCache.clear()
    }
}