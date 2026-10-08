package bsb.dev.bsb_bangking_jp.app.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.ToastHost
import bsb.dev.bsb_bangking_jp.core.components.rememberToastState
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.feature.lokasi_atm.LokasiAtmPage
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
import bsb.dev.bsb_bangking_jp.feature.transfer.PinTfPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBSBPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBaruPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBerhasilDijadwalkanPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferHomePage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferUmumPage
import bsb.dev.bsb_bangking_jp.feature.transfer.components.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.news.AllNewsPage
import androidx.compose.ui.Alignment
import bsb.dev.bsb_bangking_jp.core.components.LoadingOverlayHost
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.rememberLoadingOverlayState
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.notification.NotificationHelper
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.news.NewsDetailPage
import bsb.dev.bsb_bangking_jp.feature.transfer.toTransactionResultInfo
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage
import bsb.dev.bsb_bangking_jp.feature.activation.activationNavGraph
import bsb.dev.bsb_bangking_jp.feature.beranda.get_banner.presentation.BerandaViewModel
import bsb.dev.bsb_bangking_jp.feature.change_email.changeEmailNavGraph
import bsb.dev.bsb_bangking_jp.feature.change_mpin.changeMPinNavGraph
import bsb.dev.bsb_bangking_jp.feature.change_pw.changePwNavGraph
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.forgetIdUserNavGraph
import bsb.dev.bsb_bangking_jp.feature.forget_pw.forgetPwUserNavGraph
import bsb.dev.bsb_bangking_jp.feature.login_existing.loginExistingNavGraph
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.manageScheduledTransferNavGraph
import bsb.dev.bsb_bangking_jp.feature.notification.NotifikasiPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.FaqPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.SyaratKetentuanPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.TentangAplikasiPage
import bsb.dev.bsb_bangking_jp.feature.registration.registrationNavGraph
import bsb.dev.bsb_bangking_jp.feature.scan_qris.ScanQrisPage
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
    val context = LocalContext.current
    val navController = rememberNavController()
    val toastState = rememberToastState()
    val loadingOverlayState = rememberLoadingOverlayState()
    var pendingTransfer by remember { mutableStateOf<PeriksaKembaliData?>(null) }
    var pendingConfirmResult by remember { mutableStateOf<ConfirmTransferResultItem?>(null) }
    var pendingSumberKlasifikasi by remember { mutableStateOf("Tabungan Sekarang") }
    var pendingSumberSaldoInt by remember { mutableStateOf(0) }

    LaunchedEffect(showSessionExpired) {
        if (showSessionExpired) loadingOverlayState.hide() // jangan sampai overlay loading menggantung
    }
    if (showSessionExpired) {
        SessionExpiredDialog(
            onLoginAgain = {
                sessionGuard.onLoginAgain()
                berandaViewModel.resetLocalState() // reset Profile & Rekening ViewModel (singleton)
                navController.navigate("portal") { popUpTo(0) }
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

                composable("portal") {
                    LoginPage(navController)
                }

                composable("lokasiatm") {
                    LokasiAtmPage(
                        navController = navController,
                        onBack = {
                            navController.popBackStack()
                        },
                    )
                }
                composable("top_up") {
                    TopUpPage(
                        onNavigateToRoute = { route ->
                            navController.navigate(route)
                        },
                        onNavigateToUnavailable = {
                        },
                        onBackClick = {
                            navController.popBackStack()
                        },
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
                        }
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
                    )
                }

                composable("cardless") {
                    CardlessPage(
                        onBackClick = {
                            navController.popBackStack()
                        },
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
                        onNavigateToUnavailable = {
                        },
                    )
                }

                composable("navbar") {
                    Navbar(
                        navController = navController,
                        darkTheme = darkTheme,
                        onThemeChange = onThemeChange,
                        initialIndex = 0,
                        onNotificationClick ={navController.navigate("notifikasi")},
                        onNavigateToScanQris = {
                            navController.navigate("scan_qris")
                        },
                        onLainnyaClick = {
                            navController.navigate("menu_lainnya")
                        },
                        onTransferClick = {
                            navController.navigate("transfer_home")
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
                composable("berita_list") {
                    AllNewsPage(
                        navController = navController,
                        onBeritaClick = { item ->
                            navController.navigate("berita_detail/${item.id}")
                        },
                        onBackClick = { navController.popBackStack() },
                    )
                }

                composable(
                    route = "berita_detail/{newsId}",
                    arguments = listOf(navArgument("newsId") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val newsId = backStackEntry.arguments?.getInt("newsId") ?: 0
                    NewsDetailPage(
                        newsId = newsId,
                        onBackClick = { navController.popBackStack() },
                    )
                }


                composable("transfer_home") {
                    TransferHomePage(
                        navController = navController,
                        onBackClick = { navController.popBackStack() },
                        onTransferSekarang = { navController.navigate("transfer_baru") },
                        onAturTerjadwalClick = { navController.navigate("manage_scheduled_transfer") }, // 🔹 tambahkan
                    )
                }

                composable("transfer_baru") {
                    TransferBaruPage(
                        onBackClick = { navController.popBackStack() },
                        onContinueToNextPage = { inquiry ->
                            // 🔹 Sekarang pakai isOnUs asli dari backend, bukan cek nama bank string.
                            val destination = if (inquiry.isOnUs) "transfer_bsb" else "transfer_umum"
                            val bank = Uri.encode(inquiry.bankName)
                            val accountNumber = Uri.encode(inquiry.beneficiaryAccountNo)
                            val name = Uri.encode(inquiry.beneficiaryName)
                            navController.navigate("$destination/$bank/$accountNumber/$name")
                        },
                    )
                }

                composable(
                    route = "transfer_bsb/{bank}/{accountNumber}/{name}",
                    arguments = listOf(
                        navArgument("bank") { type = NavType.StringType },
                        navArgument("accountNumber") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                    ),
                ) { backStackEntry ->
                    val bank = backStackEntry.arguments?.getString("bank").orEmpty()
                    val accountNumber =
                        backStackEntry.arguments?.getString("accountNumber").orEmpty()
                    val name = backStackEntry.arguments?.getString("name").orEmpty()

                    TransferBSBPage(
                        bank = bank,
                        accountNumber = accountNumber,
                        name = name,
                        onBack = { navController.popBackStack() },
                        onLanjutkan = { result ->
                            pendingTransfer = PeriksaKembaliData(
                                penerimaName = name,
                                penerimaBank = bank,
                                penerimaAccountNumber = accountNumber,
                                result = result,
                            )
                            pendingSumberKlasifikasi = "Tabungan Sekarang"
                            pendingSumberSaldoInt = result.sumber.cashBalanceValue().toInt()
                            navController.navigate("pin_transfer")
                        },
                    )
                }

                composable(
                    route = "transfer_umum/{bank}/{accountNumber}/{name}",
                    arguments = listOf(
                        navArgument("bank") { type = NavType.StringType },
                        navArgument("accountNumber") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType },
                    ),
                ) { backStackEntry ->
                    val bank = backStackEntry.arguments?.getString("bank").orEmpty()
                    val accountNumber =
                        backStackEntry.arguments?.getString("accountNumber").orEmpty()
                    val name = backStackEntry.arguments?.getString("name").orEmpty()

                    TransferUmumPage(
                        bank = bank,
                        accountNumber = accountNumber,
                        name = name,
                        onBack = { navController.popBackStack() },
                        onLanjutkan = { result ->
                            pendingTransfer = PeriksaKembaliData(
                                penerimaName = name,
                                penerimaBank = bank,
                                penerimaAccountNumber = accountNumber,
                                result = result,
                            )
                            pendingSumberKlasifikasi = "Tabungan Sekarang"
                            pendingSumberSaldoInt = result.sumber.cashBalanceValue().toInt()
                            navController.navigate("pin_transfer")
                        },
                    )
                }

                composable("pin_transfer") {
                    val transferData = pendingTransfer
                    if (transferData == null) {
                        navController.popBackStack()
                    } else {
                        PinTfPage(
                            onBack = { navController.popBackStack() },
                            onBerhasilSegera = { confirmResult ->
                                pendingConfirmResult = confirmResult

                                //🔹 Trigger notifikasi + suara custom, padanan bank sungguhan.
                                NotificationHelper.showTransaksiBerhasil(
                                    context = context,
                                    title = "Transfer Berhasil",
                                    message = "Transfer ${RupiahFormat(confirmResult.totalDebit)} ke " +
                                            "${confirmResult.beneficiaryName} berhasil diproses.",
                                )

                                navController.navigate("transfer_berhasil") {
                                    popUpTo("navbar")
                                }
                            },
                            onBerhasilDijadwalkan = { confirmResult ->
                                pendingConfirmResult = confirmResult
                                navController.navigate("transfer_berhasil_dijadwalkan") {
                                    popUpTo("navbar")
                                }
                            },
                            onSessionExpired = {
                                // 🔹 Padanan pop 2x + pushReplacement(TransferPage): buang seluruh
                                // stack alur transfer (transfer_baru, transfer_bsb/umum, pin_transfer)
                                // dan mendarat balik di TransferHomePage.
                                navController.navigate("transfer_home") {
                                    popUpTo("transfer_home") { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                }

                composable("transfer_berhasil") {
                    val confirmResult = pendingConfirmResult
                    if (confirmResult == null) {
                        navController.popBackStack()
                    } else {
                        TransactionResultPage(
                            data = confirmResult.toTransactionResultInfo(),
                            onClose = {
                                navController.navigate("navbar") {
                                    popUpTo(0)
                                }
                            },
                        )
                    }
                }

                composable("transfer_berhasil_dijadwalkan") {
                    val confirmResult = pendingConfirmResult
                    if (confirmResult == null) {
                        navController.popBackStack()
                    } else {
                        TransferBerhasilDijadwalkanPage(
                            result = confirmResult,
                            sumberKlasifikasi = pendingSumberKlasifikasi,
                            sumberSaldo = pendingSumberSaldoInt,
                            onSelesai = {
                                navController.navigate("navbar") {
                                    popUpTo(0)
                                }
                            },
                        )
                    }
                }

                composable("faq") {
                    FaqPage(onBackClick = { navController.popBackStack() })
                }
                composable("syarat_ketentuan") {
                    SyaratKetentuanPage(onBackClick = { navController.popBackStack() })
                }
                composable("tentang_aplikasi") {
                    TentangAplikasiPage(onBackClick = { navController.popBackStack() })
                }
            }
            ToastHost(state = toastState, modifier = Modifier.align(Alignment.TopCenter))
            LoadingOverlayHost(state = loadingOverlayState)
        }
    }
}