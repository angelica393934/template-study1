package bsb.dev.bsb_bangking_jp.shared.profile

import bsb.dev.bsb_bangking_jp.shared.profile.data.ProfileApiService
import bsb.dev.bsb_bangking_jp.shared.profile.data.ProfilePhotoApiService
import bsb.dev.bsb_bangking_jp.shared.profile.data.ProfilePhotoRepositoryImpl
import bsb.dev.bsb_bangking_jp.shared.profile.data.ProfileRepositoryImpl
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfileRepository
import bsb.dev.bsb_bangking_jp.shared.profile.presentation.ProfileViewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val profileModule = module {
    single { get<Retrofit>().create(ProfileApiService::class.java) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }

    single { get<Retrofit>().create(ProfilePhotoApiService::class.java) }
    single<ProfilePhotoRepository> { ProfilePhotoRepositoryImpl(get(), get()) }

    single { ProfileViewModel(get(), get()) }
}