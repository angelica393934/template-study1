package bsb.dev.bsb_bangking_jp.feature.ganti_email

import bsb.dev.bsb_bangking_jp.feature.ganti_email.data.GantiEmailApiService
import bsb.dev.bsb_bangking_jp.feature.ganti_email.data.GantiEmailRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.ganti_email.domain.ConfirmGantiEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.ganti_email.domain.GantiEmailRepository
import bsb.dev.bsb_bangking_jp.feature.ganti_email.domain.GantiEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation.GantiEmailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val gantiEmailModule = module {
    single { get<Retrofit>().create(GantiEmailApiService::class.java) }
    single<GantiEmailRepository> { GantiEmailRepositoryImpl(get(), get()) }

    factory { GantiEmailUseCase(get()) }
    factory { ConfirmGantiEmailUseCase(get()) }

    // viewModel biasa, tapi di-scope ke nav graph "ganti_email" (bukan single/koinInject)
    // supaya state tetap sama antara GantiEmailPage <-> GantiEmailPinPage,
    // sama seperti pola LoginExistingViewModel.
    viewModel { GantiEmailViewModel(get(), get()) }
}