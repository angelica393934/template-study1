package bsb.dev.bsb_bangking_jp.feature.registration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AdaptiveScrollColumn
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.appLayout
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationNavEvent
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationUiEvent
import bsb.dev.bsb_bangking_jp.feature.registration.presentation.RegistrationViewModel


@Composable
fun FindAccountPageRegistration(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onNavigateToOtp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val loadingOverlay = LocalLoadingOverlay.current

    var rekeningInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf(uiState.mobileNumber) }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is RegistrationNavEvent.ToOtpPage) onNavigateToOtp()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is RegistrationUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(title = "Registrasi Akun", onBackClick = onBackClick)
            AdaptiveScrollColumn(
                modifier = Modifier
                    .weight(1f)
                    .imePadding(),                       // supaya tidak tertutup keyboard
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = appLayout.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs),
            ) {
                Image(
                    painter = painterResource(R.drawable.asset_atur_id),
                    contentDescription = "asset_atur_id"
                )
                Text(
                    text = "Registration dan Mulai Kelola Keuanganmu",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = "Lengkapi data dirimu untuk membuat akun dan nikmati kemudahan transaksi.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.extendedColors.textSecondary,
                )
                AppTextField(
                    value = rekeningInput,
                    onValueChange = {
                        rekeningInput = it
                        viewModel.clearAtmCardError()
                    },
                    labelText = "Rekening atau ATM",
                    hintText = "Masukkan Nomor Rekening / ATM",
                    icon = Icons.Default.CreditCard,
                    isNumberOnly = true,
                    errorText = uiState.atmCardError,
                    showError = uiState.atmCardError != null,
                    enableFocusBackground = true,
                )
                AppTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        viewModel.clearPhoneError()
                    },
                    labelText = "Nomor Handphone",
                    hintText = "Masukkan Nomor Handphone",
                    icon = Icons.Default.PhoneAndroid,
                    keyboardType = KeyboardType.Number,
                    isNumberOnly = true,
                    errorText = uiState.phoneInlineError,
                    showError = uiState.phoneInlineError != null,
                    enableFocusBackground = true,
                )
                Spacer(modifier = Modifier.height(appSpacing.ss))
                AppButton(
                    text = "Lanjutkan",
                    onClick = { viewModel.getAccount(rekeningInput, phoneInput) })
            }
        }
    }
}