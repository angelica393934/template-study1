package bsb.dev.bsb_bangking_jp.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.theme.White


@Composable
fun AppHeader(
    title: String,
    onBackClick: () -> Unit = {},
    showBackButton: Boolean = true,
    height: Dp = 100.dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(
                RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary)
    ) {

        Image(
            painter = painterResource(R.drawable.polaheader),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint =  White
                    )
                }
            }

            Text(
                text = title,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}