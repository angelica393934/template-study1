package bsb.dev.bsb_bangking_jp.feature.Navbar

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.feature.pengaturan.PengaturanPage
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import bsb.dev.bsb_bangking_jp.feature.activity.presentation.ActivityHistoryViewModel
import bsb.dev.bsb_bangking_jp.feature.activity.ActivityPage
import bsb.dev.bsb_bangking_jp.feature.beranda.BerandaPage
import bsb.dev.bsb_bangking_jp.feature.message.MessagePage
import org.koin.compose.koinInject
import kotlin.Unit

val NAVBAR_HEIGHT = 70.dp

private data class NavItem(
    val icon: ImageVector,
    @StringRes val labelRes: Int
)

private val navItems = listOf(
    NavItem(Icons.Default.Home, R.string.nav_home),
    NavItem(Icons.Default.TrendingUp, R.string.nav_activity),
    NavItem(Icons.Default.Email, R.string.nav_request),
    NavItem(Icons.Default.Settings, R.string.nav_settings)
)

@Composable
fun Navbar(
    navController : NavController,
    initialIndex: Int = 0,
    onNavigateToScanQris: () -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLainnyaClick: () -> Unit = {},
    onTransferClick: () -> Unit = {},
    onTopUpClick: () -> Unit = {},
    onVirtualAccountClick: () -> Unit = {},
    onBsbCashClick: () -> Unit = {},
    onPajakPendidikanClick: () -> Unit = {},
    onTagihanClick : () -> Unit = {},
    onCardlessClick : () -> Unit = {},
    activityHistoryViewModel: ActivityHistoryViewModel = koinInject(),
) {
    var currentIndex by rememberSaveable { mutableIntStateOf(initialIndex) }

    Scaffold(
        Modifier.background(Color.Transparent),
        containerColor = Color.Transparent,
        bottomBar = {
            Box {
                BankBottomBar(
                    currentIndex = currentIndex,
                    onItemSelected = { currentIndex = it }
                )

                ScanQrisFab(
                    onClick = onNavigateToScanQris,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-30).dp)
                )
            }
        }
    ){ innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    end = innerPadding.calculateEndPadding(layoutDirection)
                )
        ) {
            when (currentIndex) {
                0 -> BerandaPage(
                    navController = navController,
                    onLainnyaClick = onLainnyaClick,
                    onTransferClick = onTransferClick,
                    onTopUpClick = onTopUpClick,
                    onVirtualAccountClick = onVirtualAccountClick,
                    onBsbCashClick = onBsbCashClick,
                    onPajakPendidikanClick = onPajakPendidikanClick,
                    onTagihanClick = onTagihanClick,
                    onCardlessClick = onCardlessClick,
                )
                1 -> ActivityPage()
                2 -> MessagePage()
                3 -> PengaturanPage(
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange,
                    navController = navController,)
            }
        }
    }
}


@Composable
private fun ScanQrisFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val outerSize = 65.dp
    val innerSize = 55.dp

    Box(
        modifier = modifier
            .size(outerSize)
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.inversePrimary),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(innerSize)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),      // padding di semua sisi
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                Image(
                    painter = painterResource(R.drawable.qris),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun BankBottomBar(
    currentIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    val shadowColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 10f)

    val cornerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(NAVBAR_HEIGHT)
            .shadow(
                elevation = 200.dp,
                shape = cornerShape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(cornerShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = 5.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavBarItem(
                item = navItems[0],
                isActive = currentIndex == 0,
                modifier = Modifier.weight(1f),
                onClick = { onItemSelected(0) }
            )
            NavBarItem(
                item = navItems[1],
                isActive = currentIndex == 1,
                modifier = Modifier.weight(1f),
                onClick = { onItemSelected(1) }
            )

            // 🔹 Ruang kosong untuk FAB di tengah (setara notch)
            Box(modifier = Modifier.width(50.dp))

            NavBarItem(
                item = navItems[2],
                isActive = currentIndex == 2,
                modifier = Modifier.weight(1f),
                onClick = { onItemSelected(2) }
            )
            NavBarItem(
                item = navItems[3],
                isActive = currentIndex == 3,
                modifier = Modifier.weight(1f),
                onClick = { onItemSelected(3) }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val activeColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = stringResource(item.labelRes),
            tint = activeColor,
            modifier = Modifier.size(25.dp)
        )
        Box(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(item.labelRes),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = activeColor,
            style = MaterialTheme.typography.titleSmall
        )
    }
}