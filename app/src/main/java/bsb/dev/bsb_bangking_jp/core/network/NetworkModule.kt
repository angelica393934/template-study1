package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.BuildConfig
import bsb.dev.bsb_bangking_jp.core.network.token.RefreshTokenApiService
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRefreshInterceptor
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRepository
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRepositoryImpl
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
    // (refresh yang gagal tidak boleh coba refresh lagi).
    // ============================================================
    single(REFRESH_OKHTTP) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single(REFRESH_RETROFIT) {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get(REFRESH_OKHTTP))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>(REFRESH_RETROFIT).create(RefreshTokenApiService::class.java) }

    // ============================================================
    // 3) LAPISAN TOKEN
    // Repository menampung logika refresh. Interceptor dan
    // GetWithBodyApiHelper sama-sama memakainya.
    // ============================================================
    single<TokenRepository> {
        TokenRepositoryImpl(
            secureStorage = get(),
            // lambda, bukan instance langsung, supaya tidak ada circular dependency
            refreshApiService = { get<RefreshTokenApiService>() },
            sessionExpiredNotifier = get(),
        )
    }

    single { TokenRefreshInterceptor(tokenRepository = get()) }

    // ============================================================
    // 4) CLIENT UTAMA (semua endpoint biasa)
    // Urutan interceptor penting: token dulu, baru logging,
    // supaya log menampilkan request akhir (termasuk hasil retry).
    // ============================================================
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<TokenRefreshInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ============================================================
    // 5) HELPER GET-DENGAN-BODY (di luar OkHttp/Retrofit)
    // ============================================================
    single { GetWithBodyApiHelper(secureStorage = get(), tokenRepository = get()) }

}