package bsb.dev.bsb_bangking_jp.feature.login_existing

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AdaptiveScrollColumn
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingNavEvent
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingUiEvent
import bsb.dev.bsb_bangking_jp.feature.login_existing.presentation.LoginExistingViewModel

@Composable
fun MasukPage(
    viewModel: LoginExistingViewModel,
    onBackClick: () -> Unit,
    onNavigateToOtp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var phoneInput by remember { mutableStateOf(uiState.phoneNumber) }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is LoginExistingNavEvent.ToOtpPage) onNavigateToOtp()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is LoginExistingUiEvent.ShowToastError) {
                toastState.showError(event.message)
            }
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppHeader(
            title = "Masuk Akun",
            onBackClick = onBackClick,
        )
        AdaptiveScrollColumn(
            modifier = Modifier
                .weight(1f)
                .imePadding(),                       // supaya tidak tertutup keyboard
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = appLayout.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            ) {
            Text(
                text = "Masukkan Nomor Handphone",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Silakan masukkan nomor handphone Anda yang terdaftar untuk melanjutkan proses login.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.extendedColors.textSecondary,
            )
            AppTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                hintText = "Masukkan Nomor Handphone",
                icon = Icons.Default.PhoneAndroid,
                keyboardType = KeyboardType.Number,
                isNumberOnly = true,
                errorText = uiState.phoneInlineError,
                showError = uiState.phoneInlineError != null,
                onClearError = { viewModel.clearPhoneError() },
                enableFocusBackground = true,
            )
                Spacer(modifier = Modifier.height(appSpacing.ss))
                
            AppButton(
                text ="Lanjutkan",
                onClick = { viewModel.loginInit(phoneInput) },
            )
        }
    }
}
}