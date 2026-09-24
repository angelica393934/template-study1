package bsb.dev.bsb_bangking_jp.feature.lainnya

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.core.components.EmptyState
import bsb.dev.bsb_bangking_jp.core.components.AppMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.SearchTextField

data class LainnyaMenuItem(
    val labelRes: Int,
    val icon: ImageVector? = null,
    @DrawableRes val iconImg: Int? = null,
    val route: String? = null,
    val scale: Float? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LainnyaPage(
    navController: NavController,
    onNavigateToRoute: (String) -> Unit,
    onNavigateToUnavailable: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    var query by remember { mutableStateOf("") }
    val expandedSections = remember { mutableStateListOf<String>() }

    // ---- Menu Teratas ----
    val menuTeratas = remember {
        listOf(
            LainnyaMenuItem(
                labelRes = R.string.menu_transfer,
                iconImg = R.drawable.ic_transfer,
                route = "transfer",
                scale = 0.45f,
            ),
            LainnyaMenuItem(
                labelRes = R.string.menu_virtual_account,
                iconImg = R.drawable.ic_va1,
                scale = 0.5f,

                ),
            LainnyaMenuItem(
                labelRes = R.string.menu_bsb_cash,
                icon = Icons.Default.CreditCard,
            ),
            LainnyaMenuItem(
                labelRes = R.string.menu_cardless,
                iconImg = R.drawable.ic_cardless1,
                scale = 0.5f,
            ),
        )
    }

    // ---- Top Up ----
    val topUp = remember {
        listOf(
            LainnyaMenuItem(R.string.menu_pulsa, icon = Icons.Default.SignalCellularAlt),
            LainnyaMenuItem(R.string.menu_paket_data, icon = Icons.Default.Language),
            LainnyaMenuItem(R.string.menu_gopay, iconImg = R.drawable.ic_gopay, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_bsb_cash, iconImg = R.drawable.ic_bsb_cash1, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_ovo, iconImg = R.drawable.ic_ovo, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_shopee_pay, iconImg = R.drawable.ic_s_pay, scale = 0.6f),
        )
    }

    // ---- Tagihan ----
    val tagihan = remember {
        listOf(
            LainnyaMenuItem(R.string.menu_pdam, iconImg = R.drawable.ic_pdam, scale = 0.65f),
            LainnyaMenuItem(R.string.menu_bpjs, iconImg = R.drawable.ic_bpjs, scale = 0.8f),
            LainnyaMenuItem(R.string.menu_telkom, iconImg = R.drawable.ic_telkom, scale = 0.8f),
            LainnyaMenuItem(R.string.menu_mnc, iconImg = R.drawable.ic_mnc, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_tokopedia, iconImg = R.drawable.ic_tokopedia, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_musi, iconImg = R.drawable.ic_musi, scale = 0.6f),
            LainnyaMenuItem(R.string.menu_gas_petro, iconImg = R.drawable.ic_gas, scale = 0.75f),
            LainnyaMenuItem(R.string.menu_pusri, iconImg = R.drawable.ic_pusri, scale = 0.7f),
        )
    }

    // ---- Travel dan Tiket ----
    val travel = remember {
        listOf(
            LainnyaMenuItem(R.string.menu_kai, iconImg = R.drawable.ic_kai, scale = 0.35f),
            LainnyaMenuItem(R.string.menu_garuda, iconImg = R.drawable.ic_garuda, scale = 0.9f),
            LainnyaMenuItem(R.string.menu_lion_air, iconImg = R.drawable.ic_lion, scale = 0.8f),
        )
    }

    // ---- Pajak dan Pendidikan ----
    val pajakPendidikan = remember {
        listOf(
            LainnyaMenuItem(R.string.menu_samsat, iconImg = R.drawable.ic_samsat, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_pbb, iconImg = R.drawable.ic_pbb, scale = 0.75f),
            LainnyaMenuItem(R.string.menu_unsri, iconImg = R.drawable.ic_unsri, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_ump, iconImg = R.drawable.ic_ump, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_tridinanti, iconImg = R.drawable.ic_tridinanti, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_uin, iconImg = R.drawable.ic_uin, scale = 0.65f),
            LainnyaMenuItem(R.string.menu_pgri, iconImg = R.drawable.ic_pgri, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_stikp, iconImg = R.drawable.ic_stikp, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_stik, iconImg = R.drawable.ic_stik, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_iain, iconImg = R.drawable.ic_iain, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_ub, iconImg = R.drawable.ic_ub, scale = 0.7f),
            LainnyaMenuItem(R.string.menu_ubd, iconImg = R.drawable.ic_ubd, scale = 0.4f),
            LainnyaMenuItem(R.string.menu_ikest, iconImg = R.drawable.ic_ikest, scale = 0.7f),
        )
    }

    // ---- Ziswaf ----
    val ziswaf = remember {
        listOf(
            LainnyaMenuItem(R.string.menu_infaq, iconImg = R.drawable.ic_infaq, scale = 0.8f),
            LainnyaMenuItem(R.string.menu_wakaf, iconImg = R.drawable.ic_wakaf, scale = 0.8f),
            LainnyaMenuItem(R.string.menu_zakat, iconImg = R.drawable.ic_zakat, scale = 0.8f),
        )
    }

    val allMenus = remember(menuTeratas, topUp, tagihan, travel, pajakPendidikan, ziswaf) {
        menuTeratas + topUp + tagihan + travel + pajakPendidikan + ziswaf
    }

    val onItemTap: (LainnyaMenuItem) -> Unit = { item ->
        val route = item.route
        if (route != null) onNavigateToRoute(route) else onNavigateToUnavailable()
    }


    Scaffold(
        topBar = {
            AppHeader(
                title = stringResource(R.string.lainnya_title),
                onBackClick = {
                    onBack?.invoke() ?: navController.popBackStack()
                }
            )

        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            SearchTextField(
                value = query,
                onValueChange= { query = it },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
            val filtered = if (query.isBlank()) {
                null
            } else {
                allMenus.filter { item ->
                    stringResource(item.labelRes).contains(query, ignoreCase = true)
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (filtered != null) {
                    if (filtered.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.fillMaxSize(),
                            message = stringResource(R.string.empty_state_message),
                            subMessage = stringResource(R.string.empty_state_submessage),
                            actionText = stringResource(R.string.empty_state_action),
                            onAction = { query = "" },
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp),
                        ) {
                            MenuGrid(items = filtered, onItemTap = onItemTap)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.lainnya_section_menu_teratas),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.extendedColors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        MenuGrid(items = menuTeratas, useThemeStyle = true, onItemTap = onItemTap)
                        Spacer(modifier = Modifier.height(15.dp))

                        ExpandableMenuSection(
                            title = stringResource(R.string.lainnya_section_top_up),
                            items = topUp,
                            expanded = "top_up" in expandedSections,
                            onToggle = { toggleSection(expandedSections, "top_up") },
                            onItemTap = onItemTap,
                        )
                        ExpandableMenuSection(
                            title = stringResource(R.string.lainnya_section_tagihan),
                            items = tagihan,
                            expanded = "tagihan" in expandedSections,
                            onToggle = { toggleSection(expandedSections, "tagihan") },
                            onItemTap = onItemTap,
                        )
                        ExpandableMenuSection(
                            title = stringResource(R.string.lainnya_section_travel),
                            items = travel,
                            expanded = "travel" in expandedSections,
                            onToggle = { toggleSection(expandedSections, "travel") },
                            onItemTap = onItemTap,
                        )
                        ExpandableMenuSection(
                            title = stringResource(R.string.lainnya_section_pajak_pendidikan),
                            items = pajakPendidikan,
                            expanded = "pajak_pendidikan" in expandedSections,
                            onToggle = { toggleSection(expandedSections, "pajak_pendidikan") },
                            onItemTap = onItemTap,
                        )
                        ExpandableMenuSection(
                            title = stringResource(R.string.lainnya_section_ziswaf),
                            items = ziswaf,
                            expanded = "ziswaf" in expandedSections,
                            onToggle = { toggleSection(expandedSections, "ziswaf") },
                            onItemTap = onItemTap,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

private fun toggleSection(expanded: androidx.compose.runtime.snapshots.SnapshotStateList<String>, key: String) {
    if (key in expanded) expanded.remove(key) else expanded.add(key)
}

@Composable
private fun MenuGrid(
    modifier: Modifier = Modifier,
    items: List<LainnyaMenuItem>,
    useThemeStyle: Boolean = false,
    onItemTap: (LainnyaMenuItem) -> Unit,
) {
    val columns = 4
    val spacing = 16.dp

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val itemWidth = (maxWidth - spacing * (columns - 1)) / columns

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            maxItemsInEachRow = columns,
        ) {
            items.forEach { item ->
                Box(modifier = Modifier.width(itemWidth)) {
                    AppMenu(
                        label = stringResource(item.labelRes),
                        icon = item.icon,
                        iconImg = item.iconImg,
                        useThemeStyle = useThemeStyle,
                        scale = item.scale,
                        onTap = { onItemTap(item) },
                    )
                }
            }
        }
    }
}

/**
 * Durasi & easing animasi expand/collapse section -- dipakai bareng oleh rotasi panah
 * (animateFloatAsState) dan AnimatedVisibility supaya keduanya "senapas".
 */
private const val SectionAnimDurationMs = 500

@Composable
private fun ExpandableMenuSection(
    title: String,
    items: List<LainnyaMenuItem>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onItemTap: (LainnyaMenuItem) -> Unit,
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = SectionAnimDurationMs, easing = FastOutSlowInEasing),
        label = "lainnyaSectionArrowRotation",
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extendedColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.extendedColors.textSecondary,
                modifier = Modifier.rotate(arrowRotation),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = SectionAnimDurationMs, easing = FastOutSlowInEasing),
                expandFrom = Alignment.Top,
            ) + fadeIn(
                animationSpec = tween(durationMillis = SectionAnimDurationMs, easing = LinearOutSlowInEasing),
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = SectionAnimDurationMs, easing = FastOutSlowInEasing),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(
                animationSpec = tween(durationMillis = SectionAnimDurationMs / 2, easing = FastOutLinearInEasing),
            ),
        ) {
            MenuGrid(
                items = items,
                onItemTap = onItemTap,
                modifier = Modifier.padding(top = 12.dp, bottom = 12.dp),
            )
        }
    }
}