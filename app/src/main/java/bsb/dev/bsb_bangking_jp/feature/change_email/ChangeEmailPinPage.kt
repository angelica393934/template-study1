package bsb.dev.bsb_bangking_jp.feature.ganti_email

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.InputPinPage
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation.GantiEmailViewModel

/**
 * Padanan alur `_navigateToPinPage()` di GantiEmailPage.dart -- reuse [InputPinPage]
 * yang sudah ada (sama seperti MasukPinFlow), TANPA perlu widget PIN baru.
 * `validator` di sini LANGSUNG hit endpoint confirmchangeemail (padanan
 * `validator: (pin) async { ...await bloc.stream... }` di ) -- kalau gagal,
 * error tampil inline di halaman PIN; kalau sukses baru `onPinComplete` terpanggil.
 */
@Composable
fun GantiEmailPinPage(
    viewModel: GantiEmailViewModel,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    InputPinPage(
        title = "Masukkan M-PIN untuk melanjutkan",
        onBackClick = onBackClick,
        centerTitleWithBackButton = true,
        validator = { pin -> viewModel.confirmEmail(pin) }, // 🔥 HIT ENDPOINT 2
        onPinComplete = {
            toastState.showSuccess("Email berhasil diperbarui")
            onCompleted()
        },
    )
}