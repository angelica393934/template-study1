// feature/login_existing/LoginExistingModule.kt
package bsb.dev.bsb_bangking_jp.feature.login_existing

import bsb.dev.bsb_bangking_jp.feature.login_existing.data.LoginRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.ConfirmMpinUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.LoginInitUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.LoginExistingRepository
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.ResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.VerifyDeviceUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.domain.VerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val loginExistingModule = module {
    single<LoginExistingRepository> { LoginRepositoryImpl(get(), get(), get()) }
    factory { LoginInitUseCase(get()) }
    factory { VerifyOtpUseCase(get()) }
    factory { ResendOtpUseCase(get()) }
    factory { VerifyDeviceUseCase(get()) }
    factory { ConfirmMpinUseCase(get()) }
    viewModel { LoginExistingViewModel(get(), get(), get(), get(), get()) }
}