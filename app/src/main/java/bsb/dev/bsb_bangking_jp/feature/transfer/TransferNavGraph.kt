package bsb.dev.bsb_bangking_jp.feature.transfer

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import bsb.dev.bsb_bangking_jp.core.notification.NotificationHelper
import bsb.dev.bsb_bangking_jp.core.util.RupiahFormat
import bsb.dev.bsb_bangking_jp.feature.transfer.components.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.shared.transaction_result.TransactionResultPage
import org.koin.compose.koinInject

private val formArguments = listOf(
    navArgument("bank") { type = NavType.StringType },
    navArgument("accountNumber") { type = NavType.StringType },
    navArgument("name") { type = NavType.StringType },
)

private fun NavController.navigateToTransferForm(inquiry: TransferInquiry) {
    val destination = if (inquiry.isOnUs) "transfer_bsb" else "transfer_umum"
    navigate(
        "$destination/${Uri.encode(inquiry.bankName)}/" +
                "${Uri.encode(inquiry.beneficiaryAccountNo)}/${Uri.encode(inquiry.beneficiaryName)}"
    )
}

fun NavGraphBuilder.transferNavGraph(navController: NavController) {
    navigation(startDestination = "transfer_home", route = "transfer") {

        composable("transfer_home") {
            val flow: TransferFlowHolder = koinInject()
            // Awal siklus baru (atau kembali ke sini setelah batal/sesi habis) -> bersihkan sisa data.
            LaunchedEffect(Unit) { flow.clear() }

            TransferHomePage(
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onTransferSekarang = { navController.navigate("transfer_baru") },
                onAturTerjadwalClick = { navController.navigate("manage_scheduled_transfer") },
                onAturDaftarClick = { navController.navigate("set_saved_list") },
            )
        }

        composable("set_saved_list") {
            SetSavedListPage(onBackClick = { navController.popBackStack() })
        }

        composable("transfer_baru") {
            TransferBaruPage(
                onBackClick = { navController.popBackStack() },
                onContinueToNextPage = { navController.navigateToTransferForm(it) },
            )
        }

        composable("${"transfer_bsb"}/${"{bank}/{accountNumber}/{name}"}", formArguments) { entry ->
            val flow: TransferFlowHolder = koinInject()
            val args = entry.formArgs()
            TransferBSBPage(
                bank = args.bank, accountNumber = args.accountNumber, name = args.name,
                onBack = { navController.popBackStack() },
                onLanjutkan = { result ->
                    flow.onFormSubmitted(args.toPeriksaData(result))
                    navController.navigate("pin_transfer")
                },
            )
        }

        composable("${"transfer_umum"}/${"{bank}/{accountNumber}/{name}"}", formArguments) { entry ->
            val flow: TransferFlowHolder = koinInject()
            val args = entry.formArgs()
            TransferUmumPage(
                bank = args.bank, accountNumber = args.accountNumber, name = args.name,
                onBack = { navController.popBackStack() },
                onLanjutkan = { result ->
                    flow.onFormSubmitted(args.toPeriksaData(result))
                    navController.navigate("pin_transfer")
                },
            )
        }

        composable("pin_transfer") {
            val flow: TransferFlowHolder = koinInject()
            val context = LocalContext.current

            if (flow.pendingTransfer == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                PinTfPage(
                    onBack = { navController.popBackStack() },
                    onBerhasilSegera = { result ->
                        flow.onConfirmed(result)
                        NotificationHelper.showTransaksiBerhasil(
                            context = context,
                            title = "Transfer Berhasil",
                            message = "Transfer ${RupiahFormat(result.totalDebit)} ke " +
                                    "${result.beneficiaryName} berhasil diproses.",
                        )
                        navController.navigate("transfer_berhasil") {
                            popUpTo("navbar")
                        }
                    },
                    onBerhasilDijadwalkan = { result ->
                        flow.onConfirmed(result)
                        navController.navigate("transfer_berhasil_dijadwalkan") {
                            popUpTo("navbar")
                        }
                    },
                    onSessionExpired = {
                        navController.navigate("transfer_home") {
                            popUpTo("transfer_home") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }

        composable("transfer_berhasil") {
            val flow: TransferFlowHolder = koinInject()
            val result = flow.confirmResult
            if (result == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                TransactionResultPage(
                    data = result.toTransactionResultInfo(),
                    onClose = { navController.navigate("navbar") { popUpTo(0) } },
                )
            }
        }

        composable("transfer_berhasil_dijadwalkan") {
            val flow: TransferFlowHolder = koinInject()
            val result = flow.confirmResult
            if (result == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                TransferBerhasilDijadwalkanPage(
                    result = result,
                    sumberKlasifikasi = flow.sumberKlasifikasi,
                    sumberSaldo = flow.sumberSaldo,
                    onSelesai = { navController.navigate("navbar") { popUpTo(0) } },
                )
            }
        }
    }
}

// ---- helper kecil supaya bsb & umum tidak copy-paste ----

private data class FormArgs(val bank: String, val accountNumber: String, val name: String) {
    fun toPeriksaData(result: TransferFormResult) = PeriksaKembaliData(
        penerimaName = name,
        penerimaBank = bank,
        penerimaAccountNumber = accountNumber,
        result = result,
    )
}

private fun NavBackStackEntry.formArgs() = FormArgs(
    bank = arguments?.getString("bank").orEmpty(),
    accountNumber = arguments?.getString("accountNumber").orEmpty(),
    name = arguments?.getString("name").orEmpty(),
)