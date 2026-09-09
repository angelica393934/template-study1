package bsb.dev.bsb_bangking_jp.app.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.ToastHost
import bsb.dev.bsb_bangking_jp.core.component.rememberToastState
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.ConfirmTransferResultItem
import bsb.dev.bsb_bangking_jp.feature.lokasi_atm.LokasiAtmPage
import bsb.dev.bsb_bangking_jp.feature.Navbar.Navbar
import bsb.dev.bsb_bangking_jp.feature.bsb_cash.BsbCashHomePage
import bsb.dev.bsb_bangking_jp.feature.top_up.TopUpPage
import bsb.dev.bsb_bangking_jp.feature.va.VaPage
import bsb.dev.bsb_bangking_jp.feature.cardless.CardlessPage
import bsb.dev.bsb_bangking_jp.feature.intro.IntroPage
import bsb.dev.bsb_bangking_jp.feature.intro.IntroPage4
import bsb.dev.bsb_bangking_jp.feature.lainnya.LainnyaPage
import bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan.LainnyaPajakPage
import bsb.dev.bsb_bangking_jp.feature.pajak_pendidikan.PajakPendidikanPage
import bsb.dev.bsb_bangking_jp.feature.login.PortalPage
import bsb.dev.bsb_bangking_jp.feature.registration.BuatIdPenggunaPage
import bsb.dev.bsb_bangking_jp.feature.registration.BuatKataSandiPage
import bsb.dev.bsb_bangking_jp.feature.registration.OtpRegistrationAkunPage
import bsb.dev.bsb_bangking_jp.feature.registration.RegistrationAkunPage
import bsb.dev.bsb_bangking_jp.feature.splash.SplashScreen
import bsb.dev.bsb_bangking_jp.feature.tagihan.TagihanPage
import bsb.dev.bsb_bangking_jp.feature.transfer.PinTfPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBSBPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBaruPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferBerhasilDijadwalkanPage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferHomePage
import bsb.dev.bsb_bangking_jp.feature.transfer.TransferUmumPage
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.news.AllNewsPage
import androidx.navigation.compose.navigation
import androidx.compose.ui.Alignment
import bsb.dev.bsb_bangking_jp.core.component.LoadingOverlayHost
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.rememberLoadingOverlayState
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue
import org.koin.androidx.compose.koinViewModel
import bsb.dev.bsb_bangking_jp.feature.login_existing.MasukPage
import bsb.dev.bsb_bangking_jp.feature.login_existing.MasukPinFlow
import bsb.dev.bsb_bangking_jp.feature.login_existing.OtpMasukAkunPage
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel
import androidx.compose.ui.platform.LocalContext
import bsb.dev.bsb_bangking_jp.core.notification.NotificationHelper
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.news.NewsDetailPage
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.toTransactionResultInfo
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage

@Composable
fun AppNavigation(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val toastState = rememberToastState()
    val loadingOverlayState = rememberLoadingOverlayState()
    var pendingTransfer by remember { mutableStateOf<PeriksaKembaliData?>(null) }
    var pendingConfirmResult by remember { mutableStateOf<ConfirmTransferResultItem?>(null) }
    var pendingSumberKlasifikasi by remember { mutableStateOf("Tabungan Sekarang") }
    var pendingSumberSaldoInt by remember { mutableStateOf(0) }

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

                navigation(startDestination = "login_masuk", route = "login_existing") {

                    composable("login_masuk") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
                        val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        MasukPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("login_otp") },
                        )
                    }

                    composable("login_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
                        val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpMasukAkunPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("login_pin") },
                        )
                    }

                    composable("login_pin") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("login_existing") }
                        val viewModel: LoginExistingViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        MasukPinFlow(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCompleted = {
                                navController.navigate("navbar") {
                                    popUpTo("login_existing") { inclusive = true }
                                }
                            },
                        )
                    }
                }
//registration page
                navigation(startDestination = "registration_akun", route = "registration") {

                    composable("registration_akun") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        RegistrationAkunPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("registration_otp") },
                        )
                    }

                    composable("registration_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpRegistrationAkunPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("registration_buat_id") },
                        )
                    }

                    composable("registration_buat_id") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        BuatIdPenggunaPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToPasswordPage = { navController.navigate("registration_buat_password") },
                        )
                    }

                    composable("registration_buat_password") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        BuatKataSandiPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onRegistrationSelesai = {
                                navController.navigate("portal") { popUpTo(0) }
                            },
                        )
                    }
                }
//
                composable("portal") {
                    PortalPage(navController)
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
                            data = transferData,
                            onBack = { navController.popBackStack() },
                            onBerhasilSegera = { confirmResult ->
                                pendingConfirmResult = confirmResult

                                // 🔹 Trigger notifikasi + suara custom, padanan bank sungguhan.
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
            }
            ToastHost(state = toastState, modifier = Modifier.align(Alignment.TopCenter))
            LoadingOverlayHost(state = loadingOverlayState)
        }
    }
}