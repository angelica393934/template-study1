package bsb.dev.bsb_bangking_jp.feature.activity

import bsb.dev.bsb_bangking_jp.feature.activity.data.ActivityHistoryRepositoryImpl
import bsb.dev.bsb_bangking_jp.feature.activity.domain.ActivityHistoryRepository
import bsb.dev.bsb_bangking_jp.feature.activity.presentation.ActivityHistoryViewModel
import org.koin.dsl.module

val activityModule = module {
    single<ActivityHistoryRepository> { ActivityHistoryRepositoryImpl(get()) }
    single { ActivityHistoryViewModel(get(), get()) }
}