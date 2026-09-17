package bsb.dev.bsb_bangking_jp.core.skeleton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

/** Setara `SkeletonRekeningCard` di : judul + card rekening dengan border. */
@Composable
fun SkeletonRekeningCard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Judul "Rekening Sumber"
        SkeletonBox(width = 120.dp, height = 16.dp, borderRadius = 8.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Card isi rekening
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp))
                .border(1.dp, MaterialTheme.extendedColors.divider, RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            // Background dekoratif (pengganti SVG asli), sangat tipis
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.05f)
                    .background(MaterialTheme.extendedColors.divider),
            )

            // Isi konten utama
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            ) {
                // Info rekening
                Column {
                    SkeletonBox(width = 100.dp, height = 12.dp, borderRadius = 6.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    SkeletonBox(width = 150.dp, height = 20.dp, borderRadius = 8.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    SkeletonBox(width = 100.dp, height = 10.dp, borderRadius = 6.dp)
                }

                // Tombol "Ubah"
                SkeletonBox(width = 80.dp, height = 36.dp, borderRadius = 20.dp)
            }
        }
    }
}
