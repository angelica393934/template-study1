package bsb.dev.bsb_bangking_jp.feature.change_pw

import bsb.dev.bsb_bangking_jp.feature.change_pw.data.ChangePwApiService
import bsb.dev.bsb_bangking_jp.feature.change_pw.data.ChangePwRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.change_pw.domain.ChangePwRepository
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val changePwModule = module {
    single { get<Retrofit>().create(ChangePwApiService::class.java) }
    single<ChangePwRepository> { ChangePwRepositoryImpl(get(), get()) }
    viewModel { ChangePwViewModel(get()) }
}