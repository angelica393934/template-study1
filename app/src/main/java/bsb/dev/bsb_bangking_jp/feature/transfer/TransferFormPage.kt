package bsb.dev.bsb_bangking_jp.feature.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.InitialAvatar
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.PilihTanggalSheet
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.data.RekeningItem
import bsb.dev.bsb_bangking_jp.feature.transfer.component.JumlahTransferField
import bsb.dev.bsb_bangking_jp.feature.transfer.component.OptionItem
import bsb.dev.bsb_bangking_jp.feature.transfer.component.OptionListSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PeriksaKembaliData
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PeriksaKembaliSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PilihBulanTahunSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.component.PilihHariSheet
import bsb.dev.bsb_bangking_jp.feature.transfer.component.RekeningSumberCard
import bsb.dev.bsb_bangking_jp.feature.transfer.component.RekeningSumberUiState
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.domain.TransferRequestPayload
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferNavEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferUiEvent
import bsb.dev.bsb_bangking_jp.feature.transfer.transfer_core.presentation.TransferViewModel
import bsb.dev.bsb_bangking_jp.feature.transfer.util.TransferMapper
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Calendar

enum class TransferJenis {
    ANTAR_BANK,
    SESAMA_BSB,
}

data class TransferFormResult(
    val jumlah: Int,
    val keterangan: String,
    val isScheduled: Boolean,
    val frekuensi: String,
    val tanggal: String,
    val mulai: String,
    val sampai: String,
    val layananTransfer: String,
    val biayaLayanan: String,
    val tujuanTransfer: String,
    val sumber: RekeningItem,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferFormPage(
    jenis: TransferJenis,
    bank: String,
    accountNumber: String,
    name: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onLanjutkan: (TransferFormResult) -> Unit = {},
    transferViewModel: TransferViewModel = koinInject(),
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
) {
    var jumlah by remember { mutableStateOf(0) }
    var keterangan by remember { mutableStateOf("") }
    var isScheduled by remember { mutableStateOf(false) }
    var selectedLayanan by remember { mutableStateOf<String?>(null) }
    var selectedBiaya by remember { mutableStateOf<String?>(null) }
    var tujuanTransfer by remember { mutableStateOf("") }
    var frekuensi by remember { mutableStateOf("Sekali") }
    var tanggal by remember { mutableStateOf("") }
    var mulai by remember { mutableStateOf("") }
    var sampai by remember { mutableStateOf("") }
    var activeAccountNumber by remember { mutableStateOf<String?>(null) }
    var sumberAktif by remember { mutableStateOf<RekeningItem?>(null) }

    var showLayananSheet by remember { mutableStateOf(false) }
    var showTujuanSheet by remember { mutableStateOf(false) }
    var showFrekuensiSheet by remember { mutableStateOf(false) }
    var showHariSheet by remember { mutableStateOf(false) }
    var showKalenderPicker by remember { mutableStateOf(false) }
    var showMulaiSheet by remember { mutableStateOf(false) }
    var showSampaiSheet by remember { mutableStateOf(false) }

    var showPeriksaSheet by remember { mutableStateOf(false) }
    var pendingResult by remember { mutableStateOf<TransferFormResult?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val periksaSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val kalenderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val transferUiState by transferViewModel.uiState.collectAsStateWithLifecycle()
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()

    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    // 🔹 Loading overlay mengikuti proses submit transfer.
    LaunchedEffect(transferUiState.isSubmittingTransfer) {
        if (transferUiState.isSubmittingTransfer) loadingOverlay.show() else loadingOverlay.hide()
    }

    // 🔹 Toast untuk error non-inline (mis. sesi transfer berakhir, dsb).
    LaunchedEffect(Unit) {
        transferViewModel.uiEvent.collect { event ->
            if (event is TransferUiEvent.ShowToastError) {
                toastState.showError(event.message)
            }
        }
    }

    fun closePeriksaSheet(onClosed: () -> Unit = {}) {
        coroutineScope.launch {
            periksaSheetState.hide()
        }.invokeOnCompletion {
            if (!periksaSheetState.isVisible) {
                showPeriksaSheet = false
                onClosed()
            }
        }
    }

    LaunchedEffect(Unit) {
        transferViewModel.navEvent.collect { event ->
            when (event) {
                is TransferNavEvent.TransferSubmitted -> {
                    closePeriksaSheet {
                        pendingResult?.let { onLanjutkan(it) } // lanjut ke halaman PIN
                    }
                }
                is TransferNavEvent.TransferSessionExpired -> {
                    // 🔹 Padanan Navigator.pop(context) 2x di listener :
                    // pop 1 -> tutup PeriksaKembaliSheet, pop 2 -> keluar dari TransferFormPage.
                    closePeriksaSheet {
                        onBack()
                    }
                }
                else -> Unit
            }
        }
    }

    // 🔹 Turunkan RekeningSumberUiState dari state Beranda.
    val rekeningSumberState: RekeningSumberUiState = remember(
        rekeningUiState.isLoading,
        rekeningUiState.rekeningList,
        rekeningUiState.error,
    ) {
        when {
            rekeningUiState.isLoading && rekeningUiState.rekeningList == null ->
                RekeningSumberUiState.Loading
            rekeningUiState.rekeningList != null ->
                RekeningSumberUiState.Success(rekeningUiState.rekeningList!!)
            else ->
                RekeningSumberUiState.Error(rekeningUiState.error ?: "Data rekening tidak tersedia")
        }
    }

    val layananOptions = remember {
        listOf(
            OptionItem(
                label = "Transfer Online",
                subLabel = "Pemindahan uang antar rekening melalui layanan digital secara real-time.",
                rightText = "Rp 6.500",
            ),
            OptionItem(
                label = "Transfer BI-FAST",
                subLabel = "Layanan kirim uang antarbank cepat, aman, dan murah.",
                rightText = "Rp 2.500",
            ),
        )
    }
    val tujuanOptions = remember(transferUiState.transferPurposes) {
        if (transferUiState.transferPurposes.isNotEmpty()) {
            transferUiState.transferPurposes.map { OptionItem(it.name) }
        } else {
            listOf("Investasi", "Pemindahan Dana", "Pembelian", "Lainnya").map { OptionItem(it) }
        }
    }
    val frekuensiOptions = remember {
        listOf("Sekali", "Setiap Bulan").map { OptionItem(it) }
    }

    val isAmountValid = jumlah >= 10_000 && jumlah <= 100_000_000
    val tanggalValidUntukJadwal = if (frekuensi == "Setiap Bulan") {
        tanggal.isNotEmpty() && mulai.isNotEmpty() && sampai.isNotEmpty()
    } else {
        tanggal.isNotEmpty()
    }

    val isFormValid = when (jenis) {
        TransferJenis.SESAMA_BSB ->
            isAmountValid && (!isScheduled || tanggalValidUntukJadwal)

        TransferJenis.ANTAR_BANK ->
            isAmountValid &&
                    selectedLayanan != null &&
                    (selectedLayanan != "Transfer BI-FAST" || tujuanTransfer.isNotEmpty()) &&
                    (!isScheduled || tanggalValidUntukJadwal)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        AppHeader(
            title = if (jenis == TransferJenis.ANTAR_BANK) "Transfer Antar Bank" else "Transfer Sesama",
            onBackClick = onBack,
            height = 100.dp,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            JumlahTransferField(
                amount = jumlah,
                onAmountChange = { jumlah = it },
                minAmount = 10_000,
                maxAmount = 100_000_000
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Penerima Dana",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    InitialAvatar(initials = name)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$bank\n$accountNumber",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.extendedColors.textSecondary,
                        )
                    }
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Ganti penerima",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AppTextField(
                    value = keterangan,
                    onValueChange = { if (it.length <= 35) keterangan = it },
                    hintText = "Tambah Keterangan",
                    icon = Icons.Default.Description,
                    maxLength = 35,
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.extendedColors.divider)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    AppButton(
                        text = "Segera",
                        icon = Icons.Default.DoneAll,
                        iconBeforeText = true,
                        backgroundColor = if (!isScheduled) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.inputBackground,
                        textColor = if (!isScheduled) MaterialTheme.extendedColors.onSuccess else MaterialTheme.extendedColors.textSecondary,
                        onClick = { isScheduled = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    AppButton(
                        text = "Terjadwal",
                        icon = Icons.Default.CalendarMonth,
                        iconBeforeText = true,
                        backgroundColor = if (isScheduled) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.inputBackground,
                        textColor = if (isScheduled) MaterialTheme.extendedColors.onSuccess else MaterialTheme.extendedColors.textSecondary,
                        onClick = { isScheduled = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (jenis == TransferJenis.ANTAR_BANK) {
                    AppTextField(
                        headerLabel = "Pilih Layanan Transfer",
                        value = selectedLayanan ?: "",
                        onValueChange = {},
                        hintText = "Pilih Layanan",
                        rightDisplayText = selectedBiaya,
                        isDropdown = true,
                        onClick = { showLayananSheet = true },
                        BoldInputStyle = true,
                    )

                    if (selectedLayanan == "Transfer BI-FAST") {
                        Spacer(modifier = Modifier.height(10.dp))
                        AppTextField(
                            headerLabel = "Tujuan Transfer",
                            value = tujuanTransfer,
                            onValueChange = {},
                            hintText = "Pilih Tujuan Transfer",
                            isDropdown = true,
                            onClick = { showTujuanSheet = true },
                            BoldInputStyle = true,
                        )
                    }
                } else {
                    AppTextField(
                        headerLabel = "Layanan Transfer",
                        value = "Sesama Pengguna",
                        onValueChange = {},
                        BoldInputStyle = true,
                        rightDisplayText = "Gratis",
                        readOnly = true,
                    )
                }

                if (isScheduled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AppTextField(
                        headerLabel = "Frekuensi",
                        value = frekuensi,
                        onValueChange = {},
                        isDropdown = true,
                        onClick = { showFrekuensiSheet = true },
                        BoldInputStyle = true,
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    AppTextField(
                        headerLabel = if (frekuensi == "Setiap Bulan") "Setiap Tanggal" else "Tanggal Transaksi",
                        value = tanggal,
                        BoldInputStyle = true,
                        onValueChange = {},
                        hintText = "Pilih Tanggal",
                        isDropdown = true,
                        readOnly = true,
                        onClick = {
                            if (frekuensi == "Setiap Bulan") showHariSheet = true else showKalenderPicker = true
                        },
                    )

                    if (frekuensi == "Setiap Bulan") {
                        Spacer(modifier = Modifier.height(10.dp))
                        AppTextField(
                            BoldInputStyle = true,
                            headerLabel = "Mulai",
                            value = mulai,
                            onValueChange = {},
                            hintText = "Pilih Bulan dan Tahun",
                            isDropdown = true,
                            readOnly = true,
                            onClick = { showMulaiSheet = true },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AppTextField(
                            BoldInputStyle = true,
                            headerLabel = "Sampai Dengan",
                            value = sampai,
                            onValueChange = {},
                            hintText = "Pilih Bulan dan Tahun",
                            isDropdown = true,
                            readOnly = true,
                            onClick = { showSampaiSheet = true },
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider(color = MaterialTheme.extendedColors.divider)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                RekeningSumberCard(
                    state = rekeningSumberState,
                    activeAccountNumber = activeAccountNumber,
                    onAccountChanged = { rekening ->
                        activeAccountNumber = rekening.number
                        sumberAktif = rekening
                    },
                    onRetry = { rekeningViewModel.load(forceRefresh = true) },
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppButton(
                    text = "Lanjutkan",
                    icon =  Icons.AutoMirrored.Filled.ArrowForward,
                    enabled = isFormValid && sumberAktif != null,
                    onClick = {
                        val sumber = sumberAktif ?: return@AppButton
                        pendingResult = TransferFormResult(
                            jumlah = jumlah,
                            keterangan = keterangan,
                            isScheduled = isScheduled,
                            frekuensi = frekuensi,
                            tanggal = tanggal,
                            mulai = mulai,
                            sampai = sampai,
                            layananTransfer = selectedLayanan
                                ?: if (jenis == TransferJenis.SESAMA_BSB) "Transfer Sesama" else "-",
                            biayaLayanan = selectedBiaya
                                ?: if (jenis == TransferJenis.SESAMA_BSB) "Gratis" else "Rp 0",
                            tujuanTransfer = tujuanTransfer,
                            sumber = sumber,
                        )
                        showPeriksaSheet = true
                    },
                )
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showLayananSheet) {
        OptionListSheet(
            options = layananOptions,
            title = "Pilih Layanan Transfer",
            selectedLabel = selectedLayanan,
            onDismiss = { showLayananSheet = false },
            onSelected = { item ->
                selectedLayanan = item.label
                selectedBiaya = item.rightText
                tujuanTransfer = ""
            },
        )
    }

    if (showTujuanSheet) {
        OptionListSheet(
            options = tujuanOptions,
            title = "Tujuan Transfer",
            selectedLabel = tujuanTransfer.ifEmpty { null },
            onDismiss = { showTujuanSheet = false },
            onSelected = { tujuanTransfer = it.label },
        )
    }

    if (showFrekuensiSheet) {
        OptionListSheet(
            options = frekuensiOptions,
            title = "Frekuensi Transfer",
            selectedLabel = frekuensi,
            onDismiss = { showFrekuensiSheet = false },
            onSelected = { item ->
                frekuensi = item.label
                tanggal = ""
                if (item.label == "Sekali") {
                    mulai = ""
                    sampai = ""
                }
            },
        )
    }

    if (showHariSheet) {
        PilihHariSheet(
            selectedDate = tanggal.ifEmpty { null },
            onDismiss = { showHariSheet = false },
            onSelected = { date, isEndOfMonth ->
                tanggal = if (isEndOfMonth) "Akhir Bulan" else date
            },
        )
    }

    if (showMulaiSheet) {
        PilihBulanTahunSheet(
            onDismiss = { showMulaiSheet = false },
            onSelected = { bulan, tahun -> mulai = "$bulan $tahun" },
        )
    }

    if (showSampaiSheet) {
        PilihBulanTahunSheet(
            onDismiss = { showSampaiSheet = false },
            onSelected = { bulan, tahun -> sampai = "$bulan $tahun" },
        )
    }

    if (showKalenderPicker) {
        AppModalBottomSheet(
            sheetState = kalenderSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    kalenderSheetState.hide()
                }.invokeOnCompletion {
                    if (!kalenderSheetState.isVisible) {
                        showKalenderPicker = false
                    }
                }
            },
        ) {
            PilihTanggalSheet(
                selectedDate = LocalDate.now(),
                onDatePicked = {},
                onConfirm = { picked ->
                    tanggal = "${picked.dayOfMonth} ${bulanIndonesia[picked.monthValue - 1]} ${picked.year}"
                    coroutineScope.launch {
                        kalenderSheetState.hide()
                    }.invokeOnCompletion {
                        if (!kalenderSheetState.isVisible) {
                            showKalenderPicker = false
                        }
                    }
                },
            )
        }
    }

    if (showPeriksaSheet) {
        pendingResult?.let { result ->
            AppModalBottomSheet(
                onDismissRequest = { closePeriksaSheet() },
                sheetState = periksaSheetState,
            ) {
                PeriksaKembaliSheet(
                    data = PeriksaKembaliData(
                        penerimaName = name,
                        penerimaBank = bank,
                        penerimaAccountNumber = accountNumber,
                        result = result,
                    ),
                    isSubmitting = transferUiState.isSubmittingTransfer,
                    onConfirm = {
                        val (scheduleDate, endOfMonth) = TransferMapper.mapSchedulePayload(
                            isScheduled = result.isScheduled,
                            tanggal = result.tanggal,
                        )
                        transferViewModel.transfer(
                            TransferRequestPayload(
                                sourceAccountNo = result.sumber.number,
                                amount = result.jumlah.toDouble(),
                                service = TransferMapper.mapService(result.layananTransfer),
                                scheduleType = TransferMapper.mapScheduleType(result.isScheduled),
                                frequency = if (result.isScheduled) TransferMapper.mapFrequency(result.frekuensi) else null,
                                scheduleDate = scheduleDate,
                                endOfMonth = endOfMonth,
                                startDate = if (result.isScheduled && result.frekuensi == "Setiap Bulan")
                                    TransferMapper.formatMonthYearToEnglish(result.mulai) else null,
                                endDate = if (result.isScheduled && result.frekuensi == "Setiap Bulan")
                                    TransferMapper.formatMonthYearToEnglish(result.sampai) else null,
                                remark = result.keterangan.ifEmpty { null },
                                purpose = result.tujuanTransfer.ifEmpty { null },
                            ),
                        ) // 🔥 HIT ENDPOINT 3
                    },
                )
            }
        }
    }
}

private val bulanIndonesia = listOf(
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember",
)

private fun formatTanggalIndonesia(millis: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = bulanIndonesia[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR)
    return "$day $month $year"
}