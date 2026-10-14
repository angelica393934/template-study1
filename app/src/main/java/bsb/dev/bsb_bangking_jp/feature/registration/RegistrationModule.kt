package bsb.dev.bsb_bangking_jp.feature.registration

import bsb.dev.bsb_bangking_jp.feature.registration.data.RegistrationApiService
import bsb.dev.bsb_bangking_jp.feature.registration.data.RegistrationRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.registration.domain.RegistrationRepository
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val registrationModule = module {
    single { get<Retrofit>().create(RegistrationApiService::class.java) }
    single<RegistrationRepository> { RegistrationRepositoryImpl(get(), get(), get()) }
    viewModel { RegistrationViewModel(get()) }
}