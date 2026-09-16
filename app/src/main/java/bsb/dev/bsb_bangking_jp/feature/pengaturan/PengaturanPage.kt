
package bsb.dev.bsb_bangking_jp.feature.pengaturan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.AppHeader
import bsb.dev.bsb_bangking_jp.core.util.getAppVersion
import bsb.dev.bsb_bangking_jp.feature.beranda.presentation.BerandaViewModel
import bsb.dev.bsb_bangking_jp.feature.pengaturan.component.AccountProfileCard
import bsb.dev.bsb_bangking_jp.feature.pengaturan.component.SettingItemData
import bsb.dev.bsb_bangking_jp.feature.pengaturan.component.SettingSection
import org.koin.compose.koinInject
import bsb.dev.bsb_bangking_jp.core.skeleton.ProfileSettingSkeleton
import bsb.dev.bsb_bangking_jp.shared.logout.LogoutConfirmSheet
import bsb.dev.bsb_bangking_jp.shared.profile.presentation.ProfileViewModel

private val HeaderHeight = 100.dp
private val ProfileCardHalfHeight = 44.dp

@Composable
fun PengaturanPage(
    navController: NavController,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    profileViewModel: ProfileViewModel = koinInject(), // 🔹 sumber data profile yang sama dengan Beranda
) {
    val context = LocalContext.current
    val appVersion = remember { getAppVersion(context) }
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    // 🔹 Ambil dari response API, fallback ke DummyData kalau belum ada data (mis. belum sempat loadProfile()).
    val namaUser = uiState.profile?.user?.customerName?.takeIf { it.isNotBlank() }
        ?: uiState.profile?.external?.data?.name?.takeIf { it.isNotBlank() }
        ?: "-"

    val phoneNumber = uiState.profile?.user?.maskPhone ?: "-"
    var showLogoutSheet by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(
                    title = "",
                    showBackButton = false,
                    height = HeaderHeight,
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(ProfileCardHalfHeight + 16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (darkTheme) stringResource(R.string.theme_dark)
                            else stringResource(R.string.theme_light),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = darkTheme,
                            onCheckedChange = onThemeChange
                        )
                    }

                    SettingSection(
                        title = stringResource(R.string.section_akun),
                        items = listOf(
                            SettingItemData(
                                Icons.Default.Email,
                                stringResource(R.string.menu_email),
                                onClick = { navController.navigate("change_email") },
                            ),
                            //SettingItemData(Icons.Default.AccountBalance, stringResource(R.string.menu_kelola_rekening)),
                            SettingItemData(Icons.Default.Language, stringResource(R.string.menu_bahasa)),
                        )
                    )
                    SettingSection(
                        title = stringResource(R.string.section_keamanan),
                        items = listOf(
                            SettingItemData(
                                Icons.Default.VpnKey,
                                stringResource(R.string.menu_ganti_kata_sandi),
                                onClick = { navController.navigate("change_pw") },
                            ),
                            SettingItemData(
                                Icons.Default.VpnKey,
                                stringResource(R.string.menu_ganti_mpin),
                                onClick = { navController.navigate("change_mpin") },
                            ),
                        )
                    )
                    SettingSection(
                        title = stringResource(R.string.section_info_bantuan),
                        items = listOf(
                            SettingItemData(Icons.Default.Help, stringResource(R.string.menu_faq)),
                            SettingItemData(Icons.Default.PermDeviceInformation, stringResource(R.string.menu_syarat_ketentuan)),
                            SettingItemData(Icons.Default.PermDeviceInformation, stringResource(R.string.menu_tentang_app)),
                            SettingItemData(Icons.Default.LocationOn, stringResource(R.string.menu_lokasi_atm)),
                            SettingItemData(Icons.Default.HeadsetMic, stringResource(R.string.menu_pusat_bantuan)),
                        )
                    )
                    SettingSection(
                        title = stringResource(R.string.section_keluar),
                        items = listOf(
                            SettingItemData(Icons.Default.ExitToApp, stringResource(R.string.menu_keluar), onClick= { showLogoutSheet = true },),
                        ),

                    )

                    Text(
                        stringResource(R.string.app_version_info, "$appVersion"),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
            if (uiState.isLoading) {
                ProfileSettingSkeleton(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp)
                        .offset(y = HeaderHeight - ProfileCardHalfHeight),
                )
            } else {
                AccountProfileCard(
                    nama = namaUser,
                    phoneNumber = phoneNumber,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 16.dp)
                        .offset(y = HeaderHeight - ProfileCardHalfHeight),
                )
            }
        }
    }
    if (showLogoutSheet) {
        LogoutConfirmSheet(
            onDismiss = { showLogoutSheet = false },
            onLoggedOut = {
                showLogoutSheet = false
                // 🔥 Reset state lokal Beranda (Profile & RekeningLainnya ViewModel)
                // SETELAH SessionManager.clearSession() sukses dijalankan LogoutViewModel.
//                berandaViewModel.resetLocalState()
                navController.navigate("portal") {
                    popUpTo(0)
                }
            },
        )
    }
}

private fun Int.sp() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)