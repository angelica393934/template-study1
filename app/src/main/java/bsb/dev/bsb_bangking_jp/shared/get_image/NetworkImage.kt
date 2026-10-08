package bsb.dev.bsb_bangking_jp.shared.get_image

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import bsb.dev.bsb_bangking_jp.shared.get_image.domain.ImageCategory
import bsb.dev.bsb_bangking_jp.core.skeleton.rememberShimmerBrush
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors

@Composable
fun NetworkImage(
    path: String?,
    category: ImageCategory,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,

    ) {
    when (val state = rememberNetworkImageState(path, category)) {
        is NetworkImageState.Loading -> {
            val brush = rememberShimmerBrush()
            Box(modifier = modifier.background(brush))
        }
        is NetworkImageState.Loaded -> {
            val bitmap = remember(state.bytes) {
                BitmapFactory.decodeByteArray(state.bytes, 0, state.bytes.size).asImageBitmap()
            }
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier,
            )
        }
        is NetworkImageState.Failed -> {
            Box(
                modifier = modifier.background(extendedColors.inputBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = null,
                    tint = extendedColors.textDisabled,
                )
            }
        }
    }
}
