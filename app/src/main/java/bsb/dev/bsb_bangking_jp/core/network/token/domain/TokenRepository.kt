package bsb.dev.bsb_bangking_jp.core.network.token.domain

import bsb.dev.bsb_bangking_jp.core.network.token.TokenPhase

interface TokenRepository {
    /** Access token tersimpan untuk [phase], null kalau belum ada. */
    fun getAccessToken(phase: TokenPhase): String?

    /**
     * Refresh token untuk [phase]. [failedToken] = token yang barusan ditolak server.
     * Return access token baru, atau null kalau gagal / fase tidak bisa di-refresh.
     */
    suspend fun refresh(phase: TokenPhase, failedToken: String?): String?
}