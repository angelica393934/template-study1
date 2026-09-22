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
import bsb.dev.bsb_bangking_jp.feature.registration.CreateUserIdPageRegistration
import bsb.dev.bsb_bangking_jp.feature.registration.CreateUserPwPageRegistration
import bsb.dev.bsb_bangking_jp.feature.registration.OtpPageRegistration
import bsb.dev.bsb_bangking_jp.feature.registration.FindAccountPageRegistration
import bsb.dev.bsb_bangking_jp.feature.init.SplashScreen
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
import bsb.dev.bsb_bangking_jp.core.components.LoadingOverlayHost
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.rememberLoadingOverlayState
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.cashBalanceValue
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext
import bsb.dev.bsb_bangking_jp.core.notification.NotificationHelper
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.news.NewsDetailPage
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.toTransactionResultInfo
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage
import bsb.dev.bsb_bangking_jp.feature.activation.FindAccountPageActivation
import bsb.dev.bsb_bangking_jp.feature.activation.InputIdPageActivation
import bsb.dev.bsb_bangking_jp.feature.activation.OtpPageActivation
import bsb.dev.bsb_bangking_jp.feature.activation.InputPwPageActivation
import bsb.dev.bsb_bangking_jp.feature.activation.ActivationPinFlow
import bsb.dev.bsb_bangking_jp.feature.activation.presentation.ActivationViewModel
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.FindAccountPageForgetId
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.OtpPageForgetId
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.ResetIdPageForgetId
import bsb.dev.bsb_bangking_jp.feature.forget_iduser.presentation.ForgetIdUserViewModel
import bsb.dev.bsb_bangking_jp.feature.forget_pw.FindAccountPageForgetPw
import bsb.dev.bsb_bangking_jp.feature.forget_pw.OtpPageForgetPw
import bsb.dev.bsb_bangking_jp.feature.forget_pw.ResetPwPageForgetPw
import bsb.dev.bsb_bangking_jp.feature.forget_pw.presentation.ForgetPwViewModel
import bsb.dev.bsb_bangking_jp.feature.change_email.ChangeEmailPage
import bsb.dev.bsb_bangking_jp.feature.change_email.ChangeEmailPinPage
import bsb.dev.bsb_bangking_jp.feature.change_email.presentation.ChangeEmailViewModel
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.ManageScheduledTransferPage
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.ScheduledTransferDetailPage
import bsb.dev.bsb_bangking_jp.feature.manage_scheduled_transfer.presentation.ScheduledTransferViewModel
import bsb.dev.bsb_bangking_jp.feature.change_mpin.ChangeMpinFlow
import bsb.dev.bsb_bangking_jp.feature.change_mpin.OtpChangeMpinPage
import bsb.dev.bsb_bangking_jp.feature.change_mpin.presentation.ChangeMpinViewModel
import bsb.dev.bsb_bangking_jp.feature.change_pw.NewPasswordPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.OldPasswordPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.OtpChangePwPage
import bsb.dev.bsb_bangking_jp.feature.change_pw.presentation.ChangePwViewModel
import bsb.dev.bsb_bangking_jp.feature.login_existing.loginExistingNavGraph
import bsb.dev.bsb_bangking_jp.feature.pengaturan.FaqPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.SyaratKetentuanPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.TentangAplikasiPage
import bsb.dev.bsb_bangking_jp.feature.pengaturan.PusatBantuanPage

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
                startDestination = "splash",
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
                navigation(startDestination = "registration_akun", route = "registration") {

                    composable("registration_akun") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        FindAccountPageRegistration(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("registration_otp") },
                        )
                    }

                    composable("registration_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpPageRegistration(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("registration_buat_id") },
                        )
                    }

                    composable("registration_buat_id") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        CreateUserIdPageRegistration(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToPasswordPage = { navController.navigate("registration_buat_password") },
                        )
                    }

                    composable("registration_buat_password") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("registration") }
                        val viewModel: RegistrationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        CreateUserPwPageRegistration(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onRegistrationSelesai = {
                                navController.navigate("portal") { popUpTo(0) }
                            },
                        )
                    }
                }
                //alur activation
                navigation(startDestination = "aktivasi_akun", route = "activation") {

                    composable("aktivasi_akun") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
                        val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        FindAccountPageActivation(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToIdPengguna = { navController.navigate("aktivasi_id") },
                        )
                    }

                    composable("aktivasi_id") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
                        val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        InputIdPageActivation(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("aktivasi_otp") },
                        )
                    }

                    composable("aktivasi_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
                        val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpPageActivation(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("aktivasi_password") },
                        )
                    }

                    composable("aktivasi_password") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
                        val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        InputPwPageActivation(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToPin = { navController.navigate("aktivasi_pin") },
                        )
                    }

                    composable("aktivasi_pin") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("activation") }
                        val viewModel: ActivationViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ActivationPinFlow(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCompleted = {
                                navController.navigate("portal") { popUpTo(0) }
                            },
                        )
                    }
                }

                //lupa id user
                navigation(startDestination = "forget_iduser_home", route = "forget_iduser") {

                    composable("forget_iduser_home") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
                        val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        FindAccountPageForgetId(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("forget_iduser_otp") },
                        )
                    }

                    composable("forget_iduser_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
                        val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpPageForgetId(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("forget_iduser_reset") },
                        )
                    }

                    composable("forget_iduser_reset") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_iduser") }
                        val viewModel: ForgetIdUserViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ResetIdPageForgetId(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onResetSuccessComplete = {
                                navController.navigate("portal") { popUpTo(0) }
                            },
                        )
                    }
                }
                navigation(startDestination = "forget_pw_home", route = "forget_pw") {

                    composable("forget_pw_home") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
                        val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        FindAccountPageForgetPw(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("forget_pw_otp") },
                        )
                    }
                    // forget pw user
                    composable("forget_pw_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
                        val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpPageForgetPw(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onVerified = { navController.navigate("forget_pw_reset") },
                        )
                    }

                    composable("forget_pw_reset") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("forget_pw") }
                        val viewModel: ForgetPwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ResetPwPageForgetPw(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onResetSuccessComplete = {
                                navController.navigate("portal") { popUpTo(0) }
                            },
                        )
                    }
                }
                //
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
                        onAturTerjadwalClick = { navController.navigate("manage_scheduled_transfer") }, // 🔹 tambahkan
                    )
                }

                    // nav graph baru untuk alur transfer terjadwal
                navigation(startDestination = "manage_scheduled_transfer_list", route = "manage_scheduled_transfer") {

                    composable("manage_scheduled_transfer_list") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("manage_scheduled_transfer") }
                        val viewModel: ScheduledTransferViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ManageScheduledTransferPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onItemClick = { id -> navController.navigate("manage_scheduled_transfer_detail/$id") },
                        )
                    }

                    composable(
                        route = "manage_scheduled_transfer_detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType }),
                    ) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("manage_scheduled_transfer") }
                        val viewModel: ScheduledTransferViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
                        val id = backStackEntry.arguments?.getInt("id") ?: return@composable

                        ScheduledTransferDetailPage(
                            id = id,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                        )
                    }
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

//                                 🔹 Trigger notifikasi + suara custom, padanan bank sungguhan.
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
                // ganti email
                navigation(startDestination = "change_email_input", route = "change_email") {
                    composable("change_email_input") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_email") }
                        val viewModel: ChangeEmailViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ChangeEmailPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToPin = { navController.navigate("change_email_pin") },
                        )
                    }
                    composable("change_email_pin") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_email") }
                        val viewModel: ChangeEmailViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ChangeEmailPinPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCompleted = {
                                // padanan 3x Navigator.pop() di ChangeEmailPage.dart -- buang seluruh
                                // stack alur change_email (input + pin), balik ke PengaturanPage.
                                navController.popBackStack("change_email", inclusive = true)
                            },
                        )
                    }
                }
                //ganti pw
                navigation(startDestination = "change_pw_old", route = "change_pw") {

                    composable("change_pw_old") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
                        val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OldPasswordPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToNewPassword = { navController.navigate("change_pw_new") },
                        )
                    }

                    composable("change_pw_new") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
                        val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        NewPasswordPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("change_pw_otp") },
                        )
                    }

                    composable("change_pw_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_pw") }
                        val viewModel: ChangePwViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpChangePwPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCompleted = {
                                // 🔹 AppNavigation cuma tahu SATU hal: ke mana harus pindah setelah selesai.
                                navController.popBackStack("navbar", inclusive = false)
                            },
                        )
                    }
                }
                // ganti m-pin
                navigation(startDestination = "change_mpin_flow", route = "change_mpin") {
                    composable("change_mpin_flow") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_mpin") }
                        val viewModel: ChangeMpinViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        ChangeMpinFlow(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigateToOtp = { navController.navigate("change_mpin_otp") },
                        )
                    }
                    composable("change_mpin_otp") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("change_mpin") }
                        val viewModel: ChangeMpinViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                        OtpChangeMpinPage(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onCompleted = {
                                // 🔹 AppNavigation cuma tahu SATU hal: ke mana harus pindah setelah selesai.
                                navController.popBackStack("navbar", inclusive = false)
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
                composable("pusat_bantuan") {
                    PusatBantuanPage(onBackClick = { navController.popBackStack() })
                }
            }
            ToastHost(state = toastState, modifier = Modifier.align(Alignment.TopCenter))
            LoadingOverlayHost(state = loadingOverlayState)
        }
    }
}