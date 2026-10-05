package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
  Column yang tingginya minimal = tinggi layar yang tersedia.
  - Layar normal/besar: konten pas 1 layar, weight()/SpaceBetween tetap bekerja.
  - Layar kecil: konten melebihi layar -> otomatis bisa di-scroll.
 */
@Composable
fun AdaptiveScrollColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val minHeight = maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = minHeight),   // urutan ini penting: setelah verticalScroll
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            content = content,
        )
    }
}