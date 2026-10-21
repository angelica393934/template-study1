package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.BuildConfig
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRefreshInterceptor
import bsb.dev.bsb_bangking_jp.core.network.token.data.RefreshTokenApiService
import bsb.dev.bsb_bangking_jp.core.network.token.data.TokenRepositoryImpl
import bsb.dev.bsb_bangking_jp.core.network.token.domain.TokenRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 30L
// Qualifier dijadikan konstanta supaya tidak typo string di banyak tempat.
private val REFRESH_OKHTTP = named("refreshOkHttp")
private val REFRESH_RETROFIT = named("refreshRetrofit")

val networkModule = module {

    // ============================================================
    // 1) LOGGING
    // Dipakai bersama oleh client utama dan client refresh.
    // ============================================================
    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
            // Jangan cetak token & signature ke Logcat.
            redactHeader("Authorization")
            redactHeader("X-Signature")
        }
    }

    // ============================================================
    // 2) CLIENT REFRESH TOKEN
    // TANPA TokenRefreshInterceptor, supaya tidak infinite loop
    // (refresh yang gagal tidak boleh mencoba refresh lagi).
    // ============================================================

    // OkHttp khusus refresh.
    single(REFRESH_OKHTTP) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit khusus refresh, memakai OkHttp di atas.
    single(REFRESH_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get(REFRESH_OKHTTP))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Service endpoint refresh-token.
    single { get<Retrofit>(REFRESH_RETROFIT).create(RefreshTokenApiService::class.java) }

    // ============================================================
    // 3) LAPISAN TOKEN
    // Repository menampung seluruh logika refresh. Interceptor dan
    // GetWithBodyApiHelper sama-sama memakainya.
    // ============================================================
    single<TokenRepository> {
        TokenRepositoryImpl(
            secureStorage = get(),
            // Lambda, bukan instance langsung, supaya tidak ada circular dependency.
            refreshApiService = { get<RefreshTokenApiService>() },
            sessionExpiredNotifier = get(),
        )
    }

    // Interceptor tipis: hanya pasang Bearer, deteksi 0465, lalu retry sekali.
    single { TokenRefreshInterceptor(tokenRepository = get()) }

    // ============================================================
    // 4) CLIENT UTAMA (semua endpoint biasa lewat Retrofit)
    // Urutan interceptor penting: token dulu, baru logging,
    // supaya log menampilkan request akhir (termasuk hasil retry).
    // ============================================================

    // OkHttp utama.
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<TokenRefreshInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit utama. Semua XxxApiService dibuat dari instance ini (get<Retrofit>()).
    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ============================================================
    // 5) HELPER GET-DENGAN-BODY (di luar OkHttp/Retrofit)
    // Bypass OkHttp berarti interceptor ikut ter-bypass, jadi helper
    // ini memakai TokenRepository langsung untuk refresh + retry.
    // ============================================================
    single { GetWithBodyApiHelper(secureStorage = get(), tokenRepository = get()) }
}