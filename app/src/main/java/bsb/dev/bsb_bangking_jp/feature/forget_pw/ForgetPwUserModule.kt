package bsb.dev.bsb_bangking_jp.feature.forget_pw

import bsb.dev.bsb_bangking_jp.feature.forget_pw.data.ForgetPwApiService
import bsb.dev.bsb_bangking_jp.feature.forget_pw.data.ForgetPwRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwRepository
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.ForgetPwVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.domain.GetPwUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val forgetPwUserModule = module {
    single { get<Retrofit>().create(ForgetPwApiService::class.java) }
    single<ForgetPwRepository> { ForgetPwRepositoryImpl(get(), get()) }

    factory { GetPwUseCase(get()) }
    factory { ForgetPwVerifyOtpUseCase(get()) }
    factory { ForgetPwResendOtpUseCase(get()) }
    factory { ChangePwUseCase(get()) }

    viewModel { ForgetPwViewModel(get(), get(), get(), get()) }
}