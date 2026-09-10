package bsb.dev.bsb_bangking_jp.app

import android.app.Application
import bsb.dev.bsb_bangking_jp.app.di.sessionModule
import bsb.dev.bsb_bangking_jp.core.device.DeviceContext
import bsb.dev.bsb_bangking_jp.core.device.SecureStorageService
import bsb.dev.bsb_bangking_jp.core.device.deviceModule
import bsb.dev.bsb_bangking_jp.shared.get_image.imageModule
import bsb.dev.bsb_bangking_jp.core.network.networkModule
import bsb.dev.bsb_bangking_jp.core.notification.NotificationHelper
import bsb.dev.bsb_bangking_jp.feature.activity.activityModule
import bsb.dev.bsb_bangking_jp.feature.beranda.BerandaModule
import bsb.dev.bsb_bangking_jp.feature.init.initModule
import bsb.dev.bsb_bangking_jp.feature.login.loginModule
import bsb.dev.bsb_bangking_jp.feature.login_existing.loginExistingModule
import bsb.dev.bsb_bangking_jp.feature.message.messageModule
import bsb.dev.bsb_bangking_jp.feature.news.newsModule
import bsb.dev.bsb_bangking_jp.feature.registration.registrationModule
import bsb.dev.bsb_bangking_jp.feature.splash.splashModule
import bsb.dev.bsb_bangking_jp.feature.transfer.transferModule
import bsb.dev.bsb_bangking_jp.shared.logout.logoutModule
import bsb.dev.bsb_bangking_jp.shared.profile.profileModule
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.rekeningLainnyaModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BsbApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BsbApplication)
            modules(
                deviceModule,
                networkModule,
                initModule,
                splashModule,
                loginExistingModule,
                registrationModule,
                loginModule,
                BerandaModule,
                activityModule,
                transferModule,
                imageModule,
                newsModule,
                sessionModule,
                profileModule,
                rekeningLainnyaModule,
                logoutModule,
                messageModule,
                // tambahkan module fitur lain di sini
            )
        }

        // DeviceContext perlu SecureStorageService dari Koin, jadi diinit setelah startKoin
        DeviceContext.init(this, get<SecureStorageService>())
        NotificationHelper.createNotificationChannel(this) //untuk notif
    }
}