package bsb.dev.bsb_bangking_jp.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import bsb.dev.bsb_bangking_jp.core.theme.appLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppModalBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    showIndicator: Boolean = true,
    scrimColor: Color = Color.Black.copy(alpha = 0.6f),
    onClose: (() -> Unit)? = onDismissRequest,
    // true  = konten otomatis bisa di-scroll (default, untuk form/detail biasa)
    // false = konten mengatur scroll sendiri (mis. LazyColumn di PilihBankSheet)
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor = scrimColor,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = if (showIndicator) {
            { BottomSheetIndicator(onClose = onClose) }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                // 1) Naik mengikuti keyboard
                .imePadding()
                // 2) Scroll hanya kalau diizinkan
                .then(
                    if (scrollable) Modifier.verticalScroll(rememberScrollState())
                    else Modifier
                )
                .padding(
                    start = appLayout.defaultPadding,
                    end = appLayout.defaultPadding,
                    bottom = appLayout.defaultPadding,
                ),
            content = content,
        )
    }
}