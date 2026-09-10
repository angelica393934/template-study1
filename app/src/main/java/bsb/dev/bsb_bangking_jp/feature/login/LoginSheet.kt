// feature/portal/LoginSheet.kt
package bsb.dev.bsb_bangking_jp.feature.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Security
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
import bsb.dev.bsb_bangking_jp.core.component.AppButton
import bsb.dev.bsb_bangking_jp.core.component.AppTextField
import bsb.dev.bsb_bangking_jp.core.component.LocalLoadingOverlay
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.components.AppCheckBox
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginNavEvent
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginUiEvent
import bsb.dev.bsb_bangking_jp.feature.login.presentation.LoginViewModel
import bsb.dev.bsb_bangking_jp.shared.profile.presentation.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
    val profileViewModel: ProfileViewModel = koinInject()

    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) loadingOverlay.show() else loadingOverlay.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            if (event is LoginNavEvent.ToNavbar) {
                profileViewModel.loadProfile()
                navController.navigate("navbar") { popUpTo(0) }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is LoginUiEvent.ShowToastError) toastState.showError(event.message)
        }
    }

    // Catatan: pemblokiran back-press saat loading (padanan PopScope -mu)
    // sengaja tidak ditambahkan di sini karena AppModalBottomSheet pembungkusnya
    // sudah skipPartiallyExpanded + non-dismissable secara default saat dibutuhkan.
    // Kalau kamu mau block back-press eksplisit, tambahkan BackHandler(enabled = uiState.isLoading) {}.

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding( top=6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = loginTitle ,
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
                    tint = MaterialTheme.extendedColors.textPrimary, // padanan gray950
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
        Spacer(modifier = Modifier.height(10.dp))

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

        Spacer(modifier = Modifier.height(15.dp))

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
                color = MaterialTheme.extendedColors.cardBackground,
                text = rememberUserId,
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        AppButton(
            text = loginText,
            onClick = { viewModel.login(useridInput, passcodeInput) },
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                // TODO: buka LupaAkunBottomSheet -- belum ada padanannya di project ini
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text(
                forgotPassword,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extendedColors.divider,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AppMenu(
                icon = Icons.Outlined.Security,
                label = activation,
                onTap = {
                    navController.navigate("activation")
                   },
            )

            AppMenu(
                icon = Icons.Outlined.PersonAdd,
                label = registration,
                onTap = {
                    // TODO: navigasi ke halaman Registration Akun -- belum ada rute di project ini
                },
            )

            AppMenu(
                icon = Icons.Outlined.LocationOn,
                label = atmLocation,
                onTap = {
                    navController.navigate("lokasiatm")
                },
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}