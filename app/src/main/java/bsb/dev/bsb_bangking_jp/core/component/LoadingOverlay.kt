package bsb.dev.bsb_bangking_jp.core.component

import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

class LoadingOverlayState {
    var isVisible by mutableStateOf(false)
        private set
    var message by mutableStateOf<String?>(null)
        private set

    fun show(message: String? = null) {
        this.message = message
        isVisible = true
    }

    fun hide() {
        isVisible = false
        message = null
    }
}

@Composable
fun rememberLoadingOverlayState(): LoadingOverlayState = remember { LoadingOverlayState() }

val LocalLoadingOverlay = compositionLocalOf<LoadingOverlayState> {
    error("LoadingOverlayState belum di-provide")
}
@Composable
fun LoadingOverlayHost(state: LoadingOverlayState) {
    if (!state.isVisible) return

    Dialog(
        onDismissRequest = { /* no-op */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}, // konsumsi tap, blokir interaksi ke layar di belakangnya
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                state.message?.let {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun SideEffect(content: @Composable () -> Unit?) {
    TODO("Not yet implemented")
}