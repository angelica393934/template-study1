// feature/login_existing/LoginExistingModule.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing

import bsb.dev.bsb_bangking_jp.feature.login_existing.data.LoginExistingApiService
import bsb.dev.bsb_bangking_jp.feature.login_existing.data.LoginRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.LoginExistingRepository
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val loginExistingModule = module {
    single { get<Retrofit>().create(LoginExistingApiService::class.java) }
    single<LoginExistingRepository> { LoginRepositoryImpl(get(), get(), get()) }
    viewModel { LoginExistingViewModel(get()) }
}