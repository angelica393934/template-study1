package bsb.dev.bsb_bangking_jp.feature.change_mpin

import bsb.dev.bsb_bangking_jp.feature.change_mpin.data.ChangeMpinApiService
import bsb.dev.bsb_bangking_jp.feature.change_mpin.data.ChangeMpinRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ChangeMpinRepository
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ChangeMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.ValidateOldMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.change_mpin.domain.VerifyOtpChangeMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val changeMpinModule = module {
    single { get<Retrofit>().create(ChangeMpinApiService::class.java) }
    single<ChangeMpinRepository> { ChangeMpinRepositoryImpl(get(), get()) }

    factory { ValidateOldMpinUseCase(get()) }
    factory { ChangeMpinUseCase(get()) }
    factory { VerifyOtpChangeMpinUseCase(get()) }

    viewModel { ChangeMpinViewModel(get(), get(), get()) }
}