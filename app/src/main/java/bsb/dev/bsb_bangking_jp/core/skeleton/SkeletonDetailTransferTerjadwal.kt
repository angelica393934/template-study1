package bsb.dev.bsb_bangking_jp.core.skeleton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/**
 * Setara `SkeletonDetailTransferTerjadwal` di : header penerima,
 * baris-baris detail transfer, kartu rekening sumber, dan hint text di bawah.
 * Butuh [SkeletonRekeningCard] yang dibuat terpisah.
 */
@Composable
fun SkeletonDetailTransferTerjadwal(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        // === HEADER PENERIMA ===
        Row {
            SkeletonAvatar(size = 60.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                SkeletonBox(width = 160.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonBox(width = 140.dp, height = 12.dp)
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonBox(width = 120.dp, height = 12.dp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.divider)

        Spacer(modifier = Modifier.height(12.dp))
        // === TITLE ===
        SkeletonBox(width = 140.dp, height = 16.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // === DETAIL ROWS ===
        DetailRowSkeleton(isChip = true)
        repeat(5) { DetailRowSkeleton() }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.divider)

        Spacer(modifier = Modifier.height(4.dp))
        // === KETERANGAN ===
        DetailRowSkeleton(longValue = true)

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.extendedColors.divider)
        Spacer(modifier = Modifier.height(16.dp))

        // === REKENING SUMBER ===
        SkeletonRekeningCard()

        Spacer(modifier = Modifier.height(16.dp))

        // === HINT TEXT ===
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            SkeletonBox(width = screenWidth * 0.7f, height = 12.dp)
        }
    }
}

@Composable
private fun DetailRowSkeleton(
    isChip: Boolean = false,
    longValue: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // LABEL
        SkeletonBox(width = 120.dp, height = 12.dp)

        Spacer(modifier = Modifier.weight(1f))

        // VALUE
        if (isChip) {
            SkeletonBox(width = 60.dp, height = 22.dp, borderRadius = 20.dp)
        } else {
            SkeletonBox(width = if (longValue) 180.dp else 100.dp, height = 14.dp)
        }
    }
}
