package bsb.dev.bsb_bangking_jp.feature.change_pw

import bsb.dev.bsb_bangking_jp.feature.change_pw.data.ChangePwApiService
import bsb.dev.bsb_bangking_jp.feature.change_pw.data.ChangePwRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ChangePwRepository
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ResendOtpChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ValidateOldPwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.VerifyOtpChangePwUseCase
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val changePwModule = module {
    single { get<Retrofit>().create(ChangePwApiService::class.java) }
    single<ChangePwRepository> { ChangePwRepositoryImpl(get(), get()) }

    factory { ValidateOldPwUseCase(get()) }
    factory { ChangePwUseCase(get()) }
    factory { VerifyOtpChangePwUseCase(get()) }
    factory { ResendOtpChangePwUseCase(get()) }

    viewModel { ChangePwViewModel(get(), get(), get(), get()) }
}