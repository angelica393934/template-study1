package bsb.dev.bsb_bangking_jp.feature.init

import bsb.dev.bsb_bangking_jp.feature.init.data.InitRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.init.domain.InitDeviceUseCase
import bsb.dev.bsb_bangking_jp.feature.init.domain.InitRepository
import bsb.dev.bsb_bangking_jp.feature.init.presentation.InitViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val initModule = module {
    single<InitRepository> { InitRepositoryImpl(get(), get(), get()) }
    factory { InitDeviceUseCase(get()) }
    viewModel { InitViewModel(get(), get(), get()) }
    }