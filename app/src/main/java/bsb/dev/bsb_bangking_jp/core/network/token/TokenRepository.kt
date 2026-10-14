package bsb.dev.bsb_bangking_jp.core.network.token

interface TokenRepository {
    /** Access token yang tersimpan untuk fase ini. */
    fun getAccessToken(phase: TokenPhase): String?

    /**
     * Refresh token untuk [phase]. [failedToken] = token yang barusan ditolak server.
     * Return access token baru, atau null kalau gagal.
     */
    suspend fun refresh(phase: TokenPhase, failedToken: String?): String?
}