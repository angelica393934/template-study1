package bsb.dev.bsb_bangking_jp.feature.ganti_email

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.component.AppModalConfirm
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation.GantiEmailNavEvent
import bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation.GantiEmailUiEvent
import bsb.dev.bsb_bangking_jp.feature.ganti_email.presentation.GantiEmailViewModel

/** Padanan _validateEmail() lokal di GantiEmailPage.dart -- dicek dulu SEBELUM modal konfirmasi dibuka. */
private fun validateEmailLocal(email: String): String? {
    if (email.isBlank()) return "Email tidak boleh kosong"
    if (!Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matches(email)) return "Format email tidak valid"
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiEmailPage(
    viewModel: GantiEmailViewModel,
    onBackClick: () -> Unit,
    onNavigateToPin: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var emailInput by remember { mutableStateOf(uiState.newEmail) }
    var localEmailError by remember { mutableStateOf<String?>(null) }
    var showConfirmModal by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is GantiEmailNavEvent.ToPinPage) onNavigateToPin()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is GantiEmailUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "Email", onBackClick = onBackClick)

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(text = "Ganti Alamat Email", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Masukkan alamat email baru Anda untuk memperbarui akun.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    localEmailError = null
                    viewModel.clearEmailError()
                },
                hintText = "Alamat Email Baru",
                keyboardType = KeyboardType.Email,
                errorText = localEmailError ?: uiState.emailInlineError,
                showError = localEmailError != null || uiState.emailInlineError != null,
                onClearError = { localEmailError = null; viewModel.clearEmailError() },
                enableFocusBackground = true,
            )

            Spacer(modifier = Modifier.height(30.dp))

            AppButton(
                text = "Simpan",
                onClick = {
                    val error = validateEmailLocal(emailInput)
                    if (error != null) {
                        localEmailError = error
                    } else {
                        localEmailError = null
                        showConfirmModal = true
                    }
                },
            )
        }
    }

    if (showConfirmModal) {
        AppModalConfirm(
            onDismissRequest = { showConfirmModal = false },
            title = "Alamat email baru akan digunakan untuk menerima semua informasi penting",
            cancelText = "Batal",
            onCancel = { showConfirmModal = false },
            confirmText = "Lanjutkan",
            onConfirm = {
                showConfirmModal = false
                viewModel.gantiEmail(emailInput) // 🔥 HIT ENDPOINT 1
            },
        )
    }
}