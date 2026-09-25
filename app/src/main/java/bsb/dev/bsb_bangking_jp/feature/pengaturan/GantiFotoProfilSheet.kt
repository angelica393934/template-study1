package bsb.dev.bsb_bangking_jp.feature.pengaturan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bsb.dev.bsb_bangking_jp.core.components.AppModalBottomSheet
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import bsb.dev.bsb_bangking_jp.core.theme.extendedColors
import bsb.dev.bsb_bangking_jp.feature.set_photo_profile.presentation.PhotoProfileEvent
import bsb.dev.bsb_bangking_jp.feature.set_photo_profile.presentation.PhotoProfileSuccessAction
import bsb.dev.bsb_bangking_jp.feature.set_photo_profile.presentation.PhotoProfileViewModel
import bsb.dev.bsb_bangking_jp.feature.pengaturan.util.ImageCompressUtil
import org.koin.androidx.compose.koinViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiFotoProfilSheet(
    userId: String,
    onDismiss: () -> Unit,
    onCompleted: () -> Unit, // 🔹 dipanggil setelah update/delete sukses -- caller reload profil
    viewModel: PhotoProfileViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val toastState = LocalToastState.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cameraTempUri by remember { mutableStateOf<Uri?>(null) }

    fun handlePickedUri(uri: Uri?) {
        if (uri == null) return
        val compressedFile = try {
            ImageCompressUtil.compressIfNeeded(context, uri)
        } catch (e: Exception) {
            toastState.showError("Gagal memproses gambar")
            return
        }
        viewModel.updatePhoto(compressedFile)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri -> handlePickedUri(uri) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success -> if (success) handlePickedUri(cameraTempUri) }

    fun launchCamera() {
        val tempFile = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
        cameraTempUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            if (event is PhotoProfileEvent.Success) {
                val message = when (event.action) {
                    PhotoProfileSuccessAction.UPDATE -> "Foto profil berhasil diperbarui"
                    PhotoProfileSuccessAction.DELETE -> "Foto profil berhasil dihapus"
                }
                toastState.showSuccess(message)
                onCompleted()
                onDismiss()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            toastState.showError(it)
            viewModel.clearError()
        }
    }

    AppModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Ganti Foto Profil", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))

            PhotoOptionItem(
                icon = Icons.Default.CameraAlt,
                label = "Kamera",
                onTap = { launchCamera() },
            )
            Spacer(modifier = Modifier.height(12.dp))

            PhotoOptionItem(
                icon = Icons.Default.Image,
                label = "Galeri",
                onTap = { galleryLauncher.launch("image/*") },
            )
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.deletePhoto(userId) },
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hapus Foto Profil",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PhotoOptionItem(
    icon: ImageVector,
    label: String,
    onTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.extendedColors.divider, RoundedCornerShape(100.dp))
            .clickable { onTap() }
            .padding(vertical = 16.dp, horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = label, style = MaterialTheme.typography.titleMedium)
    }
}