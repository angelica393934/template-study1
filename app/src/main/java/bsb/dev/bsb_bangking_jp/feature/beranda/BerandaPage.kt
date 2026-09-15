package bsb.dev.bsb_bangking_jp.feature.beranda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.component.BannerKeamanan
import bsb.dev.bsb_bangking_jp.core.component.CustomRefreshIndicator
import bsb.dev.bsb_bangking_jp.core.component.LocalToastState
import bsb.dev.bsb_bangking_jp.core.component.SaldoCardEmpty
import bsb.dev.bsb_bangking_jp.feature.beranda.presentation.BerandaViewModel
import bsb.dev.bsb_bangking_jp.feature.beranda.section.HaloUserSection
import bsb.dev.bsb_bangking_jp.feature.beranda.section.MenuUtama
import bsb.dev.bsb_bangking_jp.feature.beranda.section.SaldoCardDashboard
import kotlin.math.roundToInt
import bsb.dev.bsb_bangking_jp.core.skeleton.HaloUserSkeleton
import bsb.dev.bsb_bangking_jp.core.skeleton.SkeletonSaldoCard
import bsb.dev.bsb_bangking_jp.feature.beranda.section.NewsSection
import bsb.dev.bsb_bangking_jp.shared.logout.LogoutConfirmSheet
import bsb.dev.bsb_bangking_jp.shared.profile.presentation.ProfileViewModel
import bsb.dev.bsb_bangking_jp.shared.rekening_lainnya.presentation.RekeningLainnyaViewModel
import org.koin.compose.koinInject
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

private val PULL_REFRESH_MAX_PUSH = 60.dp // 🔹 seberapa jauh konten terdorong turun saat full refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaPage(
    navController: NavController,
    onNotificationClick: () -> Unit = {},
    onTransferClick: () -> Unit = {},
    onTopUpClick: () -> Unit = {},
    onVirtualAccountClick: () -> Unit = {},
    onBsbCashClick: () -> Unit = {},
    onPajakPendidikanClick: () -> Unit = {},
    onTagihanClick: () -> Unit = {},
    onCardlessClick: () -> Unit = {},
    onLainnyaClick: () -> Unit = {},
    berandaViewModel: BerandaViewModel = koinInject(),
    profileViewModel: ProfileViewModel = koinInject(),
    rekeningViewModel: RekeningLainnyaViewModel = koinInject(),
) {
    val uiState by berandaViewModel.uiState.collectAsStateWithLifecycle()
    val toastState = LocalToastState.current
    val pullToRefreshState = rememberPullToRefreshState()
    val density = LocalDensity.current
    val berandaUiState by berandaViewModel.uiState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val rekeningUiState by rekeningViewModel.uiState.collectAsStateWithLifecycle()

    var showLogoutSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
        rekeningViewModel.load()
        berandaViewModel.loadBanner()
    }

    LaunchedEffect(berandaUiState.bannerError) {
        berandaUiState.bannerError?.let { toastState.showError(it)
        }
    }
    LaunchedEffect(profileUiState.error) {
        profileUiState.error?.let { toastState.showError(it)
        }
    }
    LaunchedEffect(rekeningUiState.error) {
        rekeningUiState.error?.let { toastState.showError(it)
        }
    }

    val namaUser = profileUiState.profile?.user?.customerName?.takeIf { it.isNotBlank() }
        ?: profileUiState.profile?.external?.data?.name?.takeIf { it.isNotBlank() }
        ?: "-"

    val isRefreshing = profileUiState.isLoading  || rekeningUiState.isRefreshing

    val maxPushPx = with(density) { PULL_REFRESH_MAX_PUSH.toPx() }
    val pushOffsetPx = if (isRefreshing) {
        maxPushPx
    } else {
        (pullToRefreshState.distanceFraction.coerceIn(0f, 1f) * maxPushPx)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Image(
            painter = painterResource(id = R.drawable.bgfull),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 60.dp),
        ) {
            if (profileUiState.isLoading) {
                HaloUserSkeleton()
            } else {
                HaloUserSection(
                    nama = namaUser,
                    photoBytes = null,
                    onNotificationClick = onNotificationClick,
                    onLogoutClick = { showLogoutSheet = true },
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { berandaViewModel.refreshAll() },
                state = pullToRefreshState,
                indicator = {
                    CustomRefreshIndicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        // 🔹 KONTEN IKUT TERDORONG TURUN sesuai jarak tarikan / status refreshing
                        .offset {
                            IntOffset(x = 0, y = pushOffsetPx.roundToInt())
                        }
                        .verticalScroll(rememberScrollState()),
                ) {
                    uiState.bannerList?.let { banners ->
                        BannerKeamanan(banners = banners) // persistKey default "beranda_pemberitahuan"
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    if (rekeningUiState.isLoading && rekeningUiState.rekeningList == null) {
                        // Loading pertama kali, belum ada data sama sekali -> skeleton
                        SkeletonSaldoCard()
                    } else {
                        // Sudah pernah ada data -> tetap tampilkan data lama walau sedang refresh
                        // (uiState.isRekeningRefreshing true tidak mengubah kondisi ini)
                        when {
                            rekeningUiState.isLoading && rekeningUiState.rekeningList == null -> {
                                // Loading pertama kali, belum ada data sama sekali -> skeleton
                                SkeletonSaldoCard()
                            }
                            rekeningUiState.rekeningList != null -> {
                                // Sudah pernah ada data -> tetap tampilkan data lama walau sedang refresh
                                // (uiState.isRekeningRefreshing true tidak mengubah kondisi ini)
                                SaldoCardDashboard(
                                    rekeningList = rekeningUiState.rekeningList!!,
                                    onSelectPrimaryAccount = { accountNumber ->
                                        rekeningViewModel.setPrimaryAccount(accountNumber)
                                    },
                                )
                            }
                            else -> {
                                // Gagal & belum pernah ada data -> card tetap ada, isinya "-"
                                SaldoCardEmpty(
                                    onRetry = { rekeningViewModel.load(forceRefresh = true) },
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    MenuUtama(
                        onTransferClick = onTransferClick,
                        onTopUpClick = onTopUpClick,
                        onVirtualAccountClick = onVirtualAccountClick,
                        onBsbCashClick = onBsbCashClick,
                        onPajakPendidikanClick = onPajakPendidikanClick,
                        onTagihanClick = onTagihanClick,
                        onCardlessClick = onCardlessClick,
                        onLainnyaClick = onLainnyaClick,
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    NewsSection(navController = navController)

                    Spacer(modifier = Modifier.height(40.dp))
                }
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
            berandaViewModel.resetLocalState()
            navController.navigate("portal") {
                popUpTo(0)
            }
        },
    )
}
}
