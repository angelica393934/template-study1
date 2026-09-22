package bsb.dev.bsb_bangking_jp.core.components

import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.delay

enum class ToastType { SUCCESS, ERROR }

data class ToastMessage(
    val message: String,
    val type: ToastType,
    val id: Long = System.currentTimeMillis(),
)

class ToastState {
    var current by mutableStateOf<ToastMessage?>(null)
        private set

    fun showSuccess(message: String) {
        current = ToastMessage(message, ToastType.SUCCESS)
    }

    fun showError(message: String) {
        current = ToastMessage(message, ToastType.ERROR)
    }

    fun dismiss() {
        current = null
    }
}

@Composable
fun rememberToastState(): ToastState = remember { ToastState() }
@Composable
fun ToastHost(
    state: ToastState,
    modifier: Modifier = Modifier,
) {
    val toast = state.current
    var lastToast by remember { mutableStateOf<ToastMessage?>(null) }
    var dialogMounted by remember { mutableStateOf(false) }

    LaunchedEffect(toast) {
        if (toast != null) {
            lastToast = toast
            dialogMounted = true
        } else if (dialogMounted) {
            delay(650)
            dialogMounted = false
        }
    }

    if (!dialogMounted) return

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.apply {
                setDimAmount(0f)
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                addFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                )
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            AnimatedVisibility(
                visible = toast != null,
                modifier = modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 10.dp, start = 24.dp, end = 24.dp),
                enter = slideInVertically(animationSpec = tween(300)) { -it },
                exit = slideOutVertically(animationSpec = tween(600)) { -it },
            ) {
                lastToast?.let { current ->
                    LaunchedEffect(current.id) {
                        delay(2000)
                        state.dismiss()
                    }

                    when (current.type) {
                        ToastType.SUCCESS -> SuccessToastContent(current.message)
                        ToastType.ERROR -> ErrorToastContent(current.message)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessToastContent(message: String) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.extendedColors.success,
                shape = RoundedCornerShape(10.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surface,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ErrorToastContent(message: String) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.extendedColors.danger,
                shape = RoundedCornerShape(10.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = null,
            tint =  MaterialTheme.colorScheme.surface,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
    }
}
val LocalToastState = androidx.compose.runtime.staticCompositionLocalOf<ToastState> {
    error("ToastState belum di-provide")
}
