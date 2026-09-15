package bsb.dev.bsb_bangking_jp.feature.change_email

import bsb.dev.bsb_bangking_jp.feature.change_email.data.ChangeEmailApiService
import bsb.dev.bsb_bangking_jp.feature.change_email.data.ChangeEmailRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.change_email.domain.ConfirmChangeEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.change_email.domain.ChangeEmailRepository
import bsb.dev.bsb_bangking_jp.feature.change_email.domain.ChangeEmailUseCase
import bsb.dev.bsb_bangking_jp.feature.change_email.presentation.ChangeEmailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val changeEmailModule = module {
    single { get<Retrofit>().create(ChangeEmailApiService::class.java) }
    single<ChangeEmailRepository> { ChangeEmailRepositoryImpl(get(), get()) }

    factory { ChangeEmailUseCase(get()) }
    factory { ConfirmChangeEmailUseCase(get()) }

    // viewModel biasa, tapi di-scope ke nav graph "change_email" (bukan single/koinInject)
    // supaya state tetap sama antara ChangeEmailPage <-> ChangeEmailPinPage,
    // sama seperti pola LoginExistingViewModel.
    viewModel { ChangeEmailViewModel(get(), get()) }
}