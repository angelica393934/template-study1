package bsb.dev.bsb_bangking_jp.app.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.ToastHost
import bsb.dev.bsb_bangking_jp.core.components.rememberToastState
import bsb.dev.bsb_bangking_jp.feature.navbar.Navbar
import bsb.dev.bsb_bangking_jp.feature.bsb_cash.BsbCashHomePage
import bsb.dev.bsb_bangking_jp.feature.top_up.TopUpPage
import bsb.dev.bsb_bangking_jp.feature.va.VaPage
import bsb.dev.bsb_bangking_jp.feature.cardless.CardlessPage
import bsb.dev.bsb_bangking_jp.feature.intro.IntroPage
import bsb.dev.bsb_bangking_jp.feature.intro.IntroPage4
import bsb.dev.bsb_bangking_jp.feature.lainnya.LainnyaPage
import bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan.LainnyaPajakPage
import bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan.PajakPendidikanPage
import bsb.dev.bsb_bangking_jp.feature.login.LoginPage
import bsb.dev.bsb_bangking_jp.feature.init.SplashScreen
import bsb.dev.bsb_bangking_jp.feature.tagihan.TagihanPage
import bsb.dev.bsb_bangking_jp.core.components.LoadingOverlayHost
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.rememberLoadingOverlayState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.feature.activation.activationNavGraph
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.presentation.BerandaViewModel
import bsb.dev.bsb_bangking_jp.feature.change_email.changeEmailNavGraph
import bsb.dev.bsb_bangking_jp.feature.change_mpin.changeMPinNavGraph
import bsb.dev.bsb_bangking_jp.feature.change_pw.changePwNavGraph
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.forgetIdUserNavGraph
import bsb.dev.bsb_bangking_jp.feature.forget_pw.forgetPwUserNavGraph
import bsb.dev.bsb_bangking_jp.feature.login_existing.loginExistingNavGraph
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.manageScheduledTransferNavGraph
import bsb.dev.bsb_bangking_jp.feature.news.newsNavGraph
import bsb.dev.bsb_bangking_jp.feature.notification.NotifikasiPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.pengaturanNavGraph
import bsb.dev.bsb_bangking_jp.feature.registration.registrationNavGraph
import bsb.dev.bsb_bangking_jp.feature.scan_qris.ScanQrisPage
import bsb.dev.bsb_bangking_jp.feature.transfer.transferNavGraph
import bsb.dev.bsb_bangking_jp.feature.underconstruction.UnderConstructionPage
import bsb.dev.bsb_bangking_jp.shared.session.SessionExpiredDialog
import bsb.dev.bsb_bangking_jp.shared.session.SessionGuard
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    val sessionGuard: SessionGuard = koinInject()
    val berandaViewModel: BerandaViewModel = koinInject()
    val showSessionExpired by sessionGuard.showSessionExpired.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val toastState = rememberToastState()
    val loadingOverlayState = rememberLoadingOverlayState()

    LaunchedEffect(showSessionExpired) {
        if (showSessionExpired) loadingOverlayState.hide() // jangan sampai overlay loading menggantung
    }
    if (showSessionExpired) {
        SessionExpiredDialog(
            onLoginAgain = {
                sessionGuard.onLoginAgain()
                berandaViewModel.resetLocalState() // reset Profile & Rekening ViewModel (singleton)
                navController.navigate("splash") { popUpTo(0) }
            },
        )
    }
    CompositionLocalProvider(LocalToastState provides toastState,
        LocalLoadingOverlay provides loadingOverlayState,
        ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "navbar",
            ) {
                composable("under_construction") {
                    UnderConstructionPage(onBackClick = { navController.popBackStack() })
                }
                composable("splash") {
                    SplashScreen(navController)
                }

                composable("intro") {
                    IntroPage(
                        navController = navController,
                        darkTheme = darkTheme,
                        onThemeChange = onThemeChange,
                    )
                }

                composable("intro4") {
                    IntroPage4(navController)
                }
                //login existing
                loginExistingNavGraph(navController)

                //registration page
                registrationNavGraph(navController)

                //activation page
                activationNavGraph(navController)

                //lupa id user
                forgetIdUserNavGraph(navController)

                //lupa pw user
                forgetPwUserNavGraph(navController)

                //manage scheduled transfer
                manageScheduledTransferNavGraph(navController)

                // ganti email
                changeEmailNavGraph(navController)

                //ganti pw
                changePwNavGraph(navController)

                // ganti m-pin
                changeMPinNavGraph(navController)

                //trasfer
                transferNavGraph(navController)

                //news
                newsNavGraph(navController)

                //pengaturan
                pengaturanNavGraph(navController)

                composable("portal") {
                    LoginPage(navController)
                }

                composable("top_up") {
                    TopUpPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onNavigateToRoute = { route ->
                            navController.navigate(route)
                        },
                        onNavigateToUnavailable = { navController.navigateToUnderConstruction() },
                    )
                }

                composable("va") {
                    VaPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                    )
                }

                composable("bsbcash") {
                    BsbCashHomePage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                    )
                }

                composable("pajak_pendidikan") {
                    PajakPendidikanPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onNavigateToRoute = { route ->
                            Log.d("NAVIGATION", "Navigate ke $route")
                            navController.navigate(route)
                        },
                        onNavigateToUnavailable = { navController.navigateToUnderConstruction()
                        },
                    )
                }

                composable("lainnya_pajak") {
                    LainnyaPajakPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                    )
                }

                composable("tagihan") {
                    TagihanPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onNavigateToUnavailable = { navController.navigateToUnderConstruction() },
                    )
                }

                composable("cardless") {
                    CardlessPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onNavigateToUnavailable = { navController.navigateToUnderConstruction() },
                    )
                }

                composable("menu_lainnya") {
                    LainnyaPage(
                        navController = navController,
                        onBack = {
                            navController.popBackStack()
                        },
                        onNavigateToRoute = { route ->
                            navController.navigate(route)
                        },
                        onNavigateToUnavailable = { navController.navigateToUnderConstruction()
                       },
                    )
                }

                composable("navbar") {
                    Navbar(
                        navController = navController,
                        initialIndex = 0,
                        onNotificationClick ={navController.navigate("notifikasi")},
                        onNavigateToScanQris = {
                            navController.navigate("scan_qris")
                        },
                        onLainnyaClick = {
                            navController.navigate("menu_lainnya")
                        },
                        onTransferClick = {
                            navController.navigate("transfer")
                        },
                        onVirtualAccountClick = {
                            navController.navigate("va")
                        },
                        onTopUpClick = {
                            navController.navigate("top_up")
                        },
                        onBsbCashClick = {
                            navController.navigate("bsbcash")
                        },
                        onPajakPendidikanClick = {
                            navController.navigate("pajak_pendidikan")
                        },
                        onTagihanClick = {
                            navController.navigate("tagihan")
                        },
                        onCardlessClick = {
                            navController.navigate("cardless")
                        }
                    )
                }
                composable("scan_qris") {
                    ScanQrisPage(
                        onBackClick = { navController.popBackStack() },
                        onResult = { qrValue ->
                            navController.popBackStack()
                        },
                    )
                }
                composable("notifikasi") {
                    NotifikasiPage(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
            ToastHost(state = toastState, modifier = Modifier.align(Alignment.TopCenter))
            LoadingOverlayHost(state = loadingOverlayState)
        }
    }
}


