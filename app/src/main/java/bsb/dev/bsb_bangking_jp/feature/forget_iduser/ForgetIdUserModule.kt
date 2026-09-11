package bsb.dev.bsb_bangking_jp.feature.forget_iduser

import bsb.dev.bsb_bangking_jp.feature.forget_iduser.data.ForgetIdUserApiService
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.data.ForgetIdUserRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ChangeIdUserUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdResendOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdUserRepository
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.ForgetIdVerifyOtpUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.domain.GetIdUserUseCase
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val forgetIdUserModule = module {
    single { get<Retrofit>().create(ForgetIdUserApiService::class.java) }
    single<ForgetIdUserRepository> { ForgetIdUserRepositoryImpl(get(), get()) }

    factory { GetIdUserUseCase(get()) }
    factory { ForgetIdVerifyOtpUseCase(get()) }
    factory { ForgetIdResendOtpUseCase(get()) }
    factory { ChangeIdUserUseCase(get()) }

    viewModel { ForgetIdUserViewModel(get(), get(), get(), get()) }
}