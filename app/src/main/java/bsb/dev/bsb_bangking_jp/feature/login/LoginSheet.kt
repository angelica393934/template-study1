package bsb.dev.bsb_bangking_jp.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppButton
import bsb.dev.bsb_bangking_jp.core.components.AppTextField
import bsb.dev.bsb_bangking_jp.core.components.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import bsb.dev.bsb_bangking_jp.core.theme.appSpacing
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginNavEvent
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginUiEvent
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginSheet(
    navController: NavController,
    onDismiss: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val loginTitle = stringResource(R.string.login_title)
    val userIdLabel = stringResource(R.string.user_id_label)
    val userIdHint = stringResource(R.string.user_id_hint)
    val passwordLabel = stringResource(R.string.password_label)
    val passwordHint = stringResource(R.string.password_hint)
    val rememberUserId = stringResource(R.string.remember_user_id)
    val loginText = stringResource(R.string.login_button)
    val forgotPassword = stringResource(R.string.forgot_userid_password)
    val activation = stringResource(R.string.menu_activation)
    val registration = stringResource(R.string.menu_registration)
    val atmLocation = stringResource(R.string.menu_atm_location)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingOverlay = LocalLoadingOverlay.current
    val toastState = LocalToastState.current

    // useridLogin dikendalikan lokal untuk field yang diketik, tapi diisi ulang
    // dari uiState.useridLogin kalau ada nilai "remembered" tersimpan.
    var useridInput by remember(uiState.useridLogin) { mutableStateOf(uiState.useridLogin) }
    var passcodeInput by remember { mutableStateOf("") }
    var showForgotAccountSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is LoginNavEvent.ToNavbar) {
                navController.navigate("navbar") { popUpTo(0) }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is LoginUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(appSpacing.xxxs)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = loginTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Tombol close (X), pojok kanan atas
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = extendedColors.textPrimary, // padanan gray950
                )
            }
        }

        AppTextField(
            value = useridInput,
            onValueChange = { useridInput = it },
            labelText = userIdLabel,
            hintText = userIdHint,
            icon = Icons.Default.Person,
            errorText = uiState.useridError,
            showError = uiState.useridError != null,
            onClearError = { viewModel.clearUseridError() },
        )

        AppTextField(
            value = passcodeInput,
            onValueChange = { passcodeInput = it },
            labelText = passwordLabel,
            hintText = passwordHint,
            icon = Icons.Default.Lock,
            obscureText = true,
            errorText = uiState.passcodeError,
            showError = uiState.passcodeError != null,
            onClearError = { viewModel.clearPasscodeError() },
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.toggleable(
                value = uiState.rememberMe,
                onValueChange = { viewModel.onRememberMeChanged(it) },
            ),
        ) {
            AppCheckBox(
                modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                value = uiState.rememberMe,
                onChanged = { viewModel.onRememberMeChanged(it) },
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                style = MaterialTheme.typography.titleSmall,
                color = extendedColors.cardBackground,
                text = rememberUserId,
            )
        }

        AppButton(
            text = loginText,
            onClick = { viewModel.login(useridInput, passcodeInput) },
        )
        TextButton(
            onClick = {
                showForgotAccountSheet = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                forgotPassword,
                style = MaterialTheme.typography.titleMedium,
                color = extendedColors.divider,
                textAlign = TextAlign.Center
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally) ,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AppMenu(
                iconImg = R.drawable.ic_regis,
                label = activation,
                useThemeStyle = true,
                circleSize = 60.dp,
                scale = 0.5f,
                onTap = {
                    navController.navigate("activation")
                },
            )

            AppMenu(
                iconImg = R.drawable.ic_activation,
                label = registration,
                circleSize = 60.dp,
                useThemeStyle = true,
                scale = 0.5f,
                onTap = {
                    navController.navigate("registration")
                        },
            )

            AppMenu(
                icon = Icons.Filled.LocationOn,
                label = atmLocation,
                circleSize = 60.dp,
                useThemeStyle = true,
                scale = 0.5f,
                onTap = {
                    navController.navigate("lokasiatm")
                },
            )
        }
    }

    if (showForgotAccountSheet) {
        ForgotAccountBottomSheet(
            onDismiss = { showForgotAccountSheet = false },
            onSelectUserId = {
                showForgotAccountSheet = false
                navController.navigate("forget_iduser")
            },
            onSelectPassword = {
                showForgotAccountSheet = false
                navController.navigate("forget_pw")
            },
        )
    }
}