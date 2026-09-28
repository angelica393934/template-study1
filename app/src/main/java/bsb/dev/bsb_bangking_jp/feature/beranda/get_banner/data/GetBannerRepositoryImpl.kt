package bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.data

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
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.BannerItem
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.GetBannerRepository
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.domain.isBanner

private const val SUCCESS_CODE = "0000"
private const val TTL_MILLIS = 24 * 60 * 60 * 1000L // padanan `ttl = Duration(hours: 24)`

class GetBannerRepositoryImpl(
    private val api: BerandaApiService,
    private val secureStorage: SecureStorageService,
) : GetBannerRepository, ClearableRepository {

    private var cache: List<BannerItem>? = null
    private var lastFetchTime: Long? = null

    override val hasData: Boolean get() = cache != null
    override val cachedBanners: List<BannerItem>? get() = cache

    override suspend fun getBanner(forceRefresh: Boolean): List<BannerItem> {
        if (!forceRefresh && hasData && !shouldRefresh()) {
            return cache!!
        }

        val fresh = retry { fetchBanner() }
        cache = fresh
        lastFetchTime = System.currentTimeMillis()
        return fresh
    }

    private fun shouldRefresh(): Boolean {
        val last = lastFetchTime ?: return true
        return System.currentTimeMillis() - last > TTL_MILLIS
    }

    private suspend fun fetchBanner(): List<BannerItem> {
        try {
            val privateKey = secureStorage.getPrivateKey()
                ?: throw IllegalStateException("Private key tidak ditemukan, device belum ter-init.")

            // GET tanpa body -> sign payload kosong, sama seperti pola getAccountSourceProfile/getDaftarBank.
            val timestamp = ApiHeaders.currentTimestamp()
            val signature = SignatureUtils.sign(emptyMap<String, String>(), timestamp, privateKey)
            val headers = ApiHeaders.withSignature(signature, ApiHeaders.full(timestamp))

            val response = api.getBanner(
                headers = headers,
                tokenPhase = TokenPhaseTag(TokenPhase.LOGIN),
            )

            if (!response.isSuccessful) {
                throw ApiErrorParser.parse(response)
            }

            val body = response.body()
            if (body?.respCode != SUCCESS_CODE) {
                throw ApiException(body?.respCode, body?.respMessage ?: "Gagal memuat banner")
            }

            // Padanan filter `.where((e) => e.isBanner)`.
            return body.data.banner
                .map { it.toDomain() }
                .filter { it.isBanner }
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException(null, NetworkErrorMapper.toUserMessage(e))
        }
    }

    override fun clear() {
        cache = null
        lastFetchTime = null
    }
}