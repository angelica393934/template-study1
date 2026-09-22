package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.transfer.component.DetailRekeningBaruModal
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PilihBankSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferInquiry
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferNavEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.daftar_bank.presentation.DaftarBankViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferBaruPage(
    onBackClick: () -> Unit = {},
    onContinueToNextPage: (TransferInquiry) -> Unit = {},
    viewModel: TransferViewModel = koinInject(),
) {
    val daftarBankViewModel: DaftarBankViewModel = koinViewModel()
    LaunchedEffect(Unit) {
        daftarBankViewModel.getDaftarBank() // no-op kalau sudah Success
    }

    val coroutineScope = rememberCoroutineScope()
    val bankSheetState = rememberModalBottomSheetState()

    var selectedBankCode by remember { mutableStateOf<String?>(null) }
    var selectedBankName by remember { mutableStateOf<String?>(null) }
    var rekeningValue by remember { mutableStateOf("") }

    var bankError by remember { mutableStateOf<String?>(null) }
    var rekeningLocalError by remember { mutableStateOf<String?>(null) } // 🔹 validasi kosong lokal

    var showBankSheet by remember { mutableStateOf(false) }
    var detailInquiry by remember { mutableStateOf<TransferInquiry?>(null) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    // 🔹 Loading overlay mengikuti proses inquiry (getAccountDest).
    LaunchedEffect(uiState.isInquiryLoading) {
        if (uiState.isInquiryLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is TransferNavEvent.ToDetailRekening) {
                detailInquiry = event.inquiry
            }
        }
    }

    // 🔹 Error toast (padanan `else -> showErrorToast(...)` di listener ).
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is TransferUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    fun closeBankSheet() {
        coroutineScope.launch {
            bankSheetState.hide()
        }.invokeOnCompletion {
            if (!bankSheetState.isVisible) {
                showBankSheet = false
            }
        }
    }

    fun validateAndContinue() {
        bankError = null
        rekeningLocalError = null
        viewModel.clearInquiryError()

        if (selectedBankCode == null) {
            bankError = "Silakan pilih bank tujuan"
        }
        if (rekeningValue.isEmpty()) {
            rekeningLocalError = "Nomor rekening tidak boleh kosong"
        }

        if (bankError != null || rekeningLocalError != null) return

        // 🔥 HIT ENDPOINT 1 TRANSFER
        viewModel.getAccountDest(code = selectedBankCode!!, accountNumber = rekeningValue)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppHeader(
                title = "Transfer Penerima Baru",
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 10.dp,
                    bottom = 10.dp,
                ),
            ) {
                AppButton(
                    text = "Lanjutkan",
                    icon =  Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = { validateAndContinue() },
                )
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                AppTextField(
                    value = selectedBankName ?: "",
                    onValueChange = {},
                    labelText = "Pilih Bank Tujuan",
                    hintText = "Bank Tujuan",
                    readOnly = true,
                    isDropdown = true,
                    errorText = bankError,
                    showError = bankError != null,
                    onClick = { showBankSheet = true },
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = rekeningValue,
                    onValueChange = { newValue ->
                        rekeningValue = newValue.filter { it.isDigit() }.take(16)
                        if (rekeningValue.isNotEmpty()) {
                            rekeningLocalError = null
                            viewModel.clearInquiryError() // 🔹 hapus error server (respCode 0602) begitu diketik ulang
                        }
                    },
                    labelText = "Nomor Rekening",
                    hintText = "Masukkan Nomor Rekening",
                    keyboardType = KeyboardType.Number,
                    maxLength = 16,
                    // 🔹 gabungkan error lokal (kosong) dengan error dari server (respCode "0602")
                    errorText = rekeningLocalError ?: uiState.inquiryError,
                    showError = rekeningLocalError != null || uiState.inquiryError != null,
                )
            }
        }
    }

    // 🔹 Sheet pilih bank tujuan
    if (showBankSheet) {
        AppModalBottomSheet(
            onDismissRequest = { closeBankSheet() },
            sheetState = bankSheetState,
        ) {
            PilihBankSheet(
                viewModel = daftarBankViewModel,
                onDismiss = { closeBankSheet() },
                onBankSelected = { code, name ->
                    selectedBankCode = code
                    selectedBankName = name
                    bankError = null
                    closeBankSheet()
                },
            )
        }
    }

    // 🔹 Sheet detail rekening, muncul setelah inquiry berhasil
    detailInquiry?.let { inquiry ->
        AppModalBottomSheet(
            onDismissRequest = { detailInquiry = null },
        ) {
            DetailRekeningBaruModal(
                inquiry = inquiry,
                viewModel = viewModel,
                onDismiss = { detailInquiry = null },
                onContinue = { finalInquiry ->
                    detailInquiry = null
                    onContinueToNextPage(finalInquiry)
                },
            )
        }
    }
}