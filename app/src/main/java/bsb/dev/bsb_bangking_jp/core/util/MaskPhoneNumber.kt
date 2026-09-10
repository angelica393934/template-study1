package bsb.dev.bsb_bangking_jp.core.util

/** Menyamarkan bagian tengah nomor HP, mis. "0812345678" -> "0812**5678". Padanan maskPhoneNumber() di Flutter. */
fun maskPhoneNumber(phoneNumber: String): String {
    if (phoneNumber.length <= 8) return phoneNumber
    val visibleStart = phoneNumber.take(4)
    val visibleEnd = phoneNumber.takeLast(4)
    return "$visibleStart${"*".repeat(phoneNumber.length - 8)}$visibleEnd"
}