package bsb.dev.bsb_bangking_jp.feature.pengaturan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/** Padanan `compressImageIfNeeded()` -- kompres kalau file lebih besar dari [maxBytes]. */
object ImageCompressUtil {
    private const val DEFAULT_MAX_BYTES = 1_500_000L // ~1.5 MB

    fun compressIfNeeded(
        context: Context,
        sourceUri: Uri,
        maxBytes: Long = DEFAULT_MAX_BYTES,
    ): File {
        val originalFile = uriToTempFile(context, sourceUri, "photo_original")

        if (originalFile.length() <= maxBytes) return originalFile

        val bitmap = BitmapFactory.decodeFile(originalFile.path) ?: return originalFile
        val outFile = File(context.cacheDir, "photo_compressed_${System.currentTimeMillis()}.jpg")

        var quality = 90
        FileOutputStream(outFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        while (outFile.length() > maxBytes && quality > 30) {
            quality -= 15
            FileOutputStream(outFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
        }
        bitmap.recycle()
        return outFile
    }

    private fun uriToTempFile(context: Context, uri: Uri, prefix: String): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Tidak bisa membaca gambar yang dipilih.")
        val tempFile = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output -> input.copyTo(output) }
        }
        return tempFile
    }
}