package bsb.dev.bsb_bangking_jp.feature.notification.data

import bsb.dev.bsb_bangking_jp.core.crypto.SignatureUtils
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.network.ApiErrorParser
import bsb.dev.bsb_bangking_jp.core.network.ApiException
import bsb.dev.bsb_bangking_jp.core.network.NetworkErrorMapper
import bsb.dev.bsb_bangking_jp.core.network.header.ApiHeaders
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase
import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhaseTag
import bsb.dev.bsb_bangking_jp.core.session.ClearableRepository
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifItem
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifRepository

private const val SUCCESS_CODE = "0000"

class NotifRepositoryImpl(
    private val api: NotifApiService,
    private val secureStorage: SecureStorageService,
) : NotifRepository, ClearableRepository {

    // 🔹 Cache in-memory, padanan `_notifCache` di GetNotifRepository (Dart).
    private var cache: List<NotifItem>? = null

    override val hasData: Boolean get() = cache != null
    override val cachedNotif: List<NotifItem>? get() = cache

    override suspend fun getNotif(forceRefresh: Boolean): List<NotifItem> {
        if (!forceRefresh && cache != null) {
            return cache!!
        }
        val fresh = fetchNotif()
        cache = fresh
        return fresh
    }

    private suspend fun fetchNotif(): List<NotifItem> {
        try {
            val privateKey = secureStorage.getPrivateKey()
                ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")

            // GET tanpa body -> sign payload kosong, sama pola dengan getAllNews/getbanner.
            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getNotif(
                headers = headers,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                throw ApiErrorParser.parse(response)
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                throw ApiException(body?.respCode, body?.respMessage ?: "Gagal memuat notifikasi.")
            }

            return body.data.map { it.toDomain() }
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