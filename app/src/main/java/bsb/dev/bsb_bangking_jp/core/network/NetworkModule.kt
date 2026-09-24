package bsb.dev.bsb_bangking_jp.core.network

import bsb.dev.bsb_bangking_jp.BuildConfig
import bsb.dev.bsb_bangking_jp.core.network.token.RefreshTokenApiService
import bsb.dev.bsb_bangking_jp.core.network.token.TokenRefreshInterceptor
import bsb.dev.bsb_bangking_jp.feature.init.data.InitApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import bsb.dev.bsb_bangking_jp.feature.login.data.LoginApiService as LoginDirectApiService
import bsb.dev.bsb_bangking_jp.feature.login_existing.data.LoginExistingApiService as LoginExistingApiService

val networkModule = module {
    single { GetWithBodyApiHelper(get(), get()) }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    // 🔹 OkHttp KHUSUS untuk refresh token -- TANPA TokenRefreshInterceptor,
    // supaya tidak infinite loop (refresh yang gagal tidak boleh coba refresh lagi).
    single(named("refreshOkHttp")) {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single(named("refreshRetrofit")) {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get(named("refreshOkHttp")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>(named("refreshRetrofit")).create(RefreshTokenApiService::class.java) }

    single { get<Retrofit>().create(LoginDirectApiService::class.java) }
    single { get<Retrofit>().create(LoginExistingApiService::class.java) }

    single {
        TokenRefreshInterceptor(
            secureStorage = get(),
            refreshApiService = { get<RefreshTokenApiService>() },
        )
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<TokenRefreshInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(InitApiService::class.java) }
    single { get<Retrofit>().create(LoginExistingApiService::class.java) }


}