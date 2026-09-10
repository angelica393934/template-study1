package bsb.dev.bsb_bangking_jp.feature.registration

import bsb.dev.bsb_bangking_jp.feature.registration.data.RegistrationApiService
import bsb.dev.bsb_bangking_jp.feature.registration.data.RegistrationRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.registration.domain.AddIdUserUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.AddPasscodeUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.GetAccountUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistVerifyDeviceUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistrationRepository
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val registrationModule = module {
    single { get<Retrofit>().create(RegistrationApiService::class.java) }
    single<RegistrationRepository> { RegistrationRepositoryImpl(get(), get(), get()) }

    factory { GetAccountUseCase(get()) }
    factory { RegistResendOtpUseCase(get()) }
    factory { RegistVerifyOtpUseCase(get()) }
    factory { RegistVerifyDeviceUseCase(get()) }
    factory { AddIdUserUseCase(get()) }
    factory { AddPasscodeUseCase(get()) }

    viewModel { RegistrationViewModel(get(), get(), get(), get(), get(), get()) }
}