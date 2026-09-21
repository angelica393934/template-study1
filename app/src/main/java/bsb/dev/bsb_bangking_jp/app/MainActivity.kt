package bsb.dev.bsb_bangking_jp.app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import bsb.dev.bsb_bangking_jp.app.navigation.AppNavigation
import bsb.dev.bsb_bangking_jp.core.notification.FcmTokenHelper
import bsb.dev.bsb_bangking_jp.core.theme.BSBBangkingJPTheme
import bsb.dev.bsb_bangking_jp.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val context = LocalContext.current

            // 🔹 Minta permission notifikasi (Android 13+ / API 33+).
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* hasil ditangani secara pasif -- jika ditolak, notif tidak akan muncul */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!granted) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // fore ntoken firebase
                FcmTokenHelper.fetchToken()
            }

            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            SideEffect {
                insetsController.isAppearanceLightStatusBars = !settings.darkTheme
                window.navigationBarColor = if (settings.darkTheme) Color.BLACK else Color.WHITE
                insetsController.isAppearanceLightNavigationBars = !settings.darkTheme
            }

            BSBBangkingJPTheme(darkTheme = settings.darkTheme) {
                Surface(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                    ),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        darkTheme = settings.darkTheme,
                        onThemeChange = { isDark -> settingsViewModel.saveTheme(isDark) }
                    )
                }
            }
        }
    }
}