package bsb.dev.bsb_bangking_jp.feature.login

import bsb.dev.bsb_bangking_jp.feature.login.data.LoginApiService
import bsb.dev.bsb_bangking_jp.feature.login.data.LoginRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.login.domain.LoginRepository
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val loginModule = module {
    single { get<Retrofit>().create(LoginApiService::class.java) }
    single<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    viewModel { LoginViewModel( get()) }
}