package bsb.dev.bsb_bangking_jp.feature.scan_qris

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.components.AppHeader
import bsb.dev.bsb_bangking_jp.core.components.LocalToastState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * Padanan ScanQrisPage.dart -- halaman pindai QRIS.
 * Preview kamera pakai CameraX, deteksi barcode pakai ML Kit (Barcode Scanning),
 * dan overlay kotak pemindai (sudut + laser berjalan) digambar manual via Canvas,
 * padanan `_ScannerOverlayPainter` (CustomPainter) di Dart.
 *
 * Dependency tambahan yang WAJIB ditambahkan ke module build.gradle(.kts) app:
 *   implementation("androidx.camera:camera-core:1.3.4")
 *   implementation("androidx.camera:camera-camera2:1.3.4")
 *   implementation("androidx.camera:camera-lifecycle:1.3.4")
 *   implementation("androidx.camera:camera-view:1.3.4")
 *   implementation("com.google.mlkit:barcode-scanning:17.3.0")
 * Dan izin kamera di AndroidManifest.xml:
 *   <uses-permission android:name="android.permission.CAMERA" />
 *   <uses-feature android:name="android.hardware.camera" android:required="false" />
 *
 * @param onBackClick dipanggil saat tombol back di header ditekan.
 * @param onResult dipanggil begitu QR/kode berhasil dipindai (nilai mentah hasil scan) --
 *   padanan `Navigator.pop(context, value)` di versi Dart.
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanQrisPage(
    onBackClick: () -> Unit = {},
    onResult: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val toastState = LocalToastState.current
    val coroutineScope = rememberCoroutineScope()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var torchOn by remember { mutableStateOf(false) }
    var handlingResult by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // 🔹 Barcode scanner ML Kit, khusus format QR Code -- padanan `formats: [BarcodeFormat.qrCode]`.
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    fun handleDetected(value: String?) {
        if (handlingResult) return
        if (value.isNullOrEmpty()) return
        handlingResult = true

        toastState.showSuccess("QR berhasil dipindai")

        coroutineScope.launch {
            delay(350) // 🔹 beri waktu animasi toast muncul, sama seperti versi Dart
            onResult(value)
        }
    }

    // 🔹 Padanan _scanFromGallery() -- pilih gambar dari galeri, lalu proses dengan ML Kit.
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val image = InputImage.fromFilePath(context, uri)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val value = barcodes.firstOrNull()?.rawValue
                    if (value != null) {
                        handleDetected(value)
                    } else {
                        toastState.showError("QR tidak ditemukan pada gambar.")
                    }
                }
                .addOnFailureListener {
                    toastState.showError("Gagal membaca gambar")
                }
        } catch (e: Exception) {
            toastState.showError("Gagal membaca gambar")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        AppHeader(
            title = "Pindai Kode QR",
            onBackClick = onBackClick,
        )

        // ===== AREA KAMERA =====
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF050505)),
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { analysisUseCase ->
                                    analysisUseCase.setAnalyzer(cameraExecutor) { imageProxy ->
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null && !handlingResult) {
                                            val inputImage = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees,
                                            )
                                            scanner.process(inputImage)
                                                .addOnSuccessListener { barcodes ->
                                                    val value = barcodes.firstOrNull()?.rawValue
                                                    if (value != null) handleDetected(value)
                                                }
                                                .addOnCompleteListener { imageProxy.close() }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                val camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    analysis,
                                )
                                cameraControl = camera.cameraControl
                            } catch (e: Exception) {
                                toastState.showError("Gagal membuka kamera")
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                )

                // ===== OVERLAY (kotak sudut + laser berjalan) =====
                ScannerOverlay(modifier = Modifier.fillMaxSize())

                // ===== LOGO QRIS DI TENGAH BAWAH KOTAK =====
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 140.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.qris),
                        contentDescription = null,
                        modifier = Modifier.size(width = 80.dp, height = 30.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }

                // ===== TOMBOL SENTER =====
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                ) {
                    RoundPillButton(
                        icon = if (torchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        onTap = {
                            torchOn = !torchOn
                            cameraControl?.enableTorch(torchOn)
                        },
                    )
                }
            } else {
                // 🔹 Kamera belum diizinkan -- tampilkan pesan + tombol minta izin lagi.
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Izin kamera diperlukan untuk memindai kode QR.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(6.dp)
                    Text(
                        text = "Ketuk untuk mengizinkan",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.clickable {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                    )
                }
            }
        }

        // ===== TOMBOL BAWAH (Pindai QR & Pilih Galeri) =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 10.dp, bottom = 25.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.padding(10.dp)) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = "Pindai Kode QR",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(
                    modifier = Modifier.clickable {
                        galleryLauncher.launch("image/*")
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(modifier = Modifier.padding(10.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = null,
                            tint = androidx.compose.ui.graphics.Color.Gray,
                        )
                    }
                    Text(
                        text = "Pilih Dari Galeri",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        color = androidx.compose.ui.graphics.Color.Gray,
                    )
                }
            }
        }
    }
}

/** Padanan `_RoundPillButton` di Dart -- tombol bulat elevated. */
@Composable
private fun RoundPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onTap() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

/**
 * Padanan `_ScannerOverlayPainter` (CustomPainter) di Dart -- kotak pemindai dengan
 * border di 4 sudut saja (bukan garis penuh) yang berkedip pelan, plus garis laser
 * bergradasi yang bergerak naik-turun.
 */
@Composable
private fun ScannerOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_overlay")

    // 🔹 Padanan _borderCtrl (lowerBound 0.2, upperBound 1.0, reverse).
    val borderGlow by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "border_glow",
    )

    // 🔹 Padanan _laserCtrl (0..1, reverse).
    val laserPos by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "laser_pos",
    )

    Canvas(modifier = modifier) {
        val boxSize = size.width * 0.65f
        val center = Offset(size.width / 2f, size.height / 2f)
        val left = center.x - boxSize / 2f
        val top = center.y - boxSize / 2f
        val right = center.x + boxSize / 2f
        val bottom = center.y + boxSize / 2f
        val corner = 24.dp.toPx()
        val strokeWidth = 3.dp.toPx()

        val borderColor = Color.White.copy(alpha = 0.9f * borderGlow)

        fun line(from: Offset, to: Offset) {
            drawLine(color = borderColor, start = from, end = to, strokeWidth = strokeWidth)
        }

        // kiri atas
        line(Offset(left, top), Offset(left + corner, top))
        line(Offset(left, top), Offset(left, top + corner))
        // kanan atas
        line(Offset(right, top), Offset(right - corner, top))
        line(Offset(right, top), Offset(right, top + corner))
        // kiri bawah
        line(Offset(left, bottom), Offset(left + corner, bottom))
        line(Offset(left, bottom), Offset(left, bottom - corner))
        // kanan bawah
        line(Offset(right, bottom), Offset(right - corner, bottom))
        line(Offset(right, bottom), Offset(right, bottom - corner))

        // laser bergerak naik-turun dengan gradasi warna
        val laserY = top + 10.dp.toPx() + (bottom - top - 20.dp.toPx()) * laserPos
        val laserBrush = Brush.horizontalGradient(
            colors = listOf(
                Color(0x0076F7C3),
                Color(0xFF4486D8),
                Color(0x00F0EDED),
            ),
            startX = left,
            endX = right,
        )
        drawLine(
            brush = laserBrush,
            start = Offset(left + 6.dp.toPx(), laserY),
            end = Offset(right - 6.dp.toPx(), laserY),
            strokeWidth = 2.5.dp.toPx(),
        )
    }
}

@Composable
private fun Spacer(height: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(height))
}