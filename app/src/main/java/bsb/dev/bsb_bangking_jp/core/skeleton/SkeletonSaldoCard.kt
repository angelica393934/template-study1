package bsb.dev.bsb_bangking_jp.core.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/** Setara `SkeletonSaldoCard` di : card saldo dengan rekening, nilai besar, dan footer. */
@Composable
fun SkeletonSaldoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            // Nomor rekening + tombol salin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SkeletonBox(width = 150.dp, height = 12.dp, borderRadius = 6.dp)
                Spacer(modifier = Modifier.width(16.dp))
                SkeletonBox(width = 60.dp, height = 12.dp, borderRadius = 6.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Label "Saldo Sekarang"
            SkeletonBox(width = 120.dp, height = 12.dp, borderRadius = 6.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Nilai saldo (besar) + ikon mata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBox(
                    height = 28.dp,
                    borderRadius = 8.dp,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(width = 24.dp, height = 24.dp, borderRadius = 6.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color =  extendedColors.strip)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: klasifikasi + tombol "Rekening Lainnya"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SkeletonBox(width = 100.dp, height = 12.dp, borderRadius = 6.dp)
                SkeletonBox(width = 120.dp, height = 12.dp, borderRadius = 6.dp)
            }
        }
    }
}
