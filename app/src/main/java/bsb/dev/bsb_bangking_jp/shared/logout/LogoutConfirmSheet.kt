package bsb.dev.bsb_bangking_jp.shared.logout

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.shared.logout.presentation.LogoutUiState
import bsb.dev.bsb_bangking_jp.shared.logout.presentation.LogoutViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Padanan LogoutConfirmSheet.dart -- bottom sheet konfirmasi keluar aplikasi.
 * Dipanggil dari mana pun (mis. HaloUserSection.onLogoutClick di Beranda, atau
 * menu "Keluar" di PengaturanPage).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutConfirmSheet(
    onDismiss: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: LogoutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LogoutUiState.Loading -> loadingOverlay.show()
            is LogoutUiState.Success -> {
                loadingOverlay.hide()
                onLoggedOut()
            }
            is LogoutUiState.Failure -> {
                loadingOverlay.hide()
                toastState.showError(state.respMessage)
            }
            is LogoutUiState.Initial -> loadingOverlay.hide()
        }
    }

    AppModalConfirm(
        onDismissRequest = onDismiss,
        topimage = R.drawable.logout,
        title = "Apakah Anda yakin ingin keluar dari aplikasi?",
        cancelText = "Batal",
        onCancel = onDismiss,
        confirmText = "Keluar",
        onConfirm = { viewModel.logout() },
    )
}