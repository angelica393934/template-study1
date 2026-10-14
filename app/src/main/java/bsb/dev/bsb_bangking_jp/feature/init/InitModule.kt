package bsb.dev.bsb_bangking_jp.feature.init

import bsb.dev.bsb_bangking_jp.feature.init.data.InitApiService
import bsb.dev.bsb_bangking_jp.feature.init.data.InitRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.init.domain.InitRepository
import bsb.dev.bsb_bangking_jp.feature.init.presentation.InitViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val initModule = module {
    single { get<Retrofit>().create(InitApiService::class.java) }
    single<InitRepository> { InitRepositoryImpl(get(), get(), get()) }
    viewModel { InitViewModel(get(),  get(),get()) }
    }