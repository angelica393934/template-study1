package bsb.dev.bsb_bangking_jp.core.network.header

import bsb.dev.bsb_bangking_jp.core.device.DeviceContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object ApiHeaders {

    private object Keys {
        const val TIMESTAMP = "X-TIMESTAMP"
        const val DEVICE_ID = "Device-ID"
        const val CONTENT_TYPE = "Content-Type"
        const val DEVICE_NAME = "Device-Name"
        const val OS = "Os"
        const val OS_VERSION = "Os-Version"
        const val APP_VERSION = "App-Version"
        const val SIGNATURE = "X-Signature"
    }

    // 🔹 Offset WIB eksplisit, BUKAN literal string di pattern -- supaya timestamp
    // selalu WIB yang benar walau timezone device di-set beda (atau salah).
    private val WIB_OFFSET = ZoneOffset.of("+07:00")

    private val timestampFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

    // 🔹 Instant.now() = waktu absolut (UTC epoch), TIDAK bergantung timezone device sama sekali.
    // atOffset(WIB_OFFSET) baru mengkonversinya jadi representasi jam WIB.
    fun currentTimestamp(): String = Instant.now().atOffset(WIB_OFFSET).format(timestampFormatter)

    fun full(timestamp: String = currentTimestamp()): Map<String, String> = linkedMapOf(
        Keys.TIMESTAMP to timestamp,
        Keys.DEVICE_ID to DeviceContext.deviceId,
        Keys.CONTENT_TYPE to "application/json",
        Keys.DEVICE_NAME to DeviceContext.deviceName,
        Keys.OS to DeviceContext.os,
        Keys.OS_VERSION to DeviceContext.osVersion,
        Keys.APP_VERSION to DeviceContext.appVersion,
    )

    fun withoutAppVersionAndOs(): Map<String, String> = full() - Keys.APP_VERSION - Keys.OS

    fun withSignature(signature: String, base: Map<String, String> = full()): Map<String, String> =
        base + (Keys.SIGNATURE to signature)

    fun minimal(): Map<String, String> = linkedMapOf(
        Keys.CONTENT_TYPE to "application/json",
        Keys.DEVICE_ID to DeviceContext.deviceId,
    )

    fun fullWithoutContentType(
        timestamp: String = currentTimestamp()
    ): Map<String, String> = linkedMapOf(
        Keys.TIMESTAMP to timestamp,
        Keys.DEVICE_ID to DeviceContext.deviceId,
        Keys.DEVICE_NAME to DeviceContext.deviceName,
        Keys.OS to DeviceContext.os,
        Keys.OS_VERSION to DeviceContext.osVersion,
        Keys.APP_VERSION to DeviceContext.appVersion,
    )
}