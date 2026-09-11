package bsb.dev.bsb_bangking_jp.core.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    showIndicator: Boolean = true,
    scrimColor: Color = Color.Black.copy(alpha = 0.6f),
    onClose: (() -> Unit)? = onDismissRequest,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor= scrimColor,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = if (showIndicator) {
            {
                BottomSheetIndicator(
                    onClose = onClose,
                )
            }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
            content = content,
        )
    }
}