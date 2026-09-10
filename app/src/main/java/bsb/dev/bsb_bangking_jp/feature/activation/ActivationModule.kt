package bsb.dev.bsb_bangking_jp.feature.activation

import bsb.dev.bsb_bangking_jp.feature.activation.data.ActivationApiService
import bsb.dev.bsb_bangking_jp.feature.activation.data.ActivationRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationRepository
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ActivationVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ConfirmMpinActivationUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.GetAccountActivationUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ValidatePasscodeUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.domain.ValidateUserIdUseCase
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val activationModule = module {
    single { get<Retrofit>().create(ActivationApiService::class.java) }
    single<ActivationRepository> { ActivationRepositoryImpl(get(), get(), get()) }

    factory { GetAccountActivationUseCase(get()) }
    factory { ValidateUserIdUseCase(get()) }
    factory { ActivationResendOtpUseCase(get()) }
    factory { ActivationVerifyOtpUseCase(get()) }
    factory { ValidatePasscodeUseCase(get()) }
    factory { ConfirmMpinActivationUseCase(get()) }

    viewModel { ActivationViewModel(get(), get(), get(), get(), get(), get()) }
}