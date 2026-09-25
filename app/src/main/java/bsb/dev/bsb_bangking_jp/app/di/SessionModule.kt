package bsb.dev.bsb_bangking_jp.app.di

import bsb.dev.bsb_bangking_jp.core.session.ClearableRepository
import bsb.dev.bsb_bangking_jp.core.session.SessionClearer
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageRepository
import bsb.dev.bsb_bangking_jp.feature.activity.domain.ActivityHistoryRepository
import bsb.dev.bsb_bangking_jp.feature.beranda.domain.get_banner.GetBannerRepository
import bsb.dev.bsb_bangking_jp.feature.news.domain.AllNewsRepository
import bsb.dev.bsb_bangking_jp.feature.news.domain.NewsDetailRepository
import bsb.dev.bsb_bangking_jp.feature.news.domain.NewsRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.last_transfer.domain.LastTransferRepository
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.domain.ScheduledTransferRepository
import bsb.dev.bsb_bangking_jp.feature.notification.domain.NotifRepository
import bsb.dev.bsb_bangking_jp.feature.transfer.saved_recipient.domain.SavedRecipientRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfilePhotoRepository
import bsb.dev.bsb_bangking_jp.shared.profile.domain.ProfileRepository
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.domain.RekeningLainnyaRepository
import org.koin.dsl.module

val sessionModule = module {
    single {
        SessionClearer(
            repositories = listOf(
                get<ProfileRepository>() as ClearableRepository,
                get<ProfilePhotoRepository>() as ClearableRepository,
                get<RekeningLainnyaRepository>() as ClearableRepository,
                get<ActivityHistoryRepository>() as ClearableRepository,
                get<ScheduledTransferRepository>() as ClearableRepository,
                get<LastTransferRepository>() as ClearableRepository,
                get<SavedRecipientRepository>() as ClearableRepository,
                get<GetBannerRepository>() as ClearableRepository,
                get<NewsRepository>() as ClearableRepository,
                get<AllNewsRepository>() as ClearableRepository,
                get<NewsDetailRepository>() as ClearableRepository,
                get<ImageRepository>() as ClearableRepository,
                get<NotifRepository>() as ClearableRepository,
            )
        )
    }
}