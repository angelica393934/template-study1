package bsb.dev.bsb_bangking_jp.core.dummy
import androidx.annotation.DrawableRes
import bsb.dev.bsb_bangking_jp.R

/**
 * Dummy untuk 1 item metode/riwayat pembayaran (padanan `PaymentItemData` di ),
 * dipakai di bagian "Daftar Pembayaran Terakhir" pada TopUpPage & VaPage & PajakPendidikanPage.
 */
data class DummyPaymentMethod(
    val title: String,
    val number: String,
    @DrawableRes val iconRes: Int,
)

/**
 * Dummy untuk 1 entri menu berbasis ikon+label (padanan `MenuItemData` / `Map` menu di
 * ), dipakai di grid menu PajakPendidikanPage dan daftar LainnyaPajakPage.
 * `route` null berarti menu belum tersedia (fallback ke halaman "fitur belum ada").
 */
data class DummyMenuIcon(
    val label: String,
    @DrawableRes val iconRes: Int,
    val scale: Float = 0.7f,
    val route: String? = null,
)

/**
 * Dummy untuk 1 entri riwayat transaksi cardless (padanan `CardlessHistory` di ),
 * dipakai di CardlessPage.
 */
data class DummyCardlessHistory(
    val title: String,
    val date: String,
    /** "Berhasil" / "Kadaluwarsa" dll -- menentukan warna badge status. */
    val status: String,
)

object DummyData {
    val paymentMethodList = listOf(
    DummyPaymentMethod("BSB Cash", "1234560", R.drawable.ic_bsb_cash1,),
    DummyPaymentMethod("OVO", "1738742343", R.drawable.ic_ovo),
    DummyPaymentMethod("Gopay", "1234567890", R.drawable.ic_gopay),
    DummyPaymentMethod("Shopee Pay", "1843725862", R.drawable.ic_s_pay),
    )


    val pajakMenuItems = listOf(
    DummyMenuIcon("SAMSAT", R.drawable.ic_samsat, 0.7f),
    DummyMenuIcon("Pajak Bumi dan Bangunan", R.drawable.ic_pbb, 0.7f),
    DummyMenuIcon("Universitas Sriwijaya", R.drawable.ic_unsri, 0.7f),
    DummyMenuIcon("Universitas Muhammadiyah Palembang", R.drawable.ic_ump, 0.7f),
    DummyMenuIcon("Universitas Tridinanti", R.drawable.ic_tridinanti, 0.7f),
    DummyMenuIcon("UIN Raden\nFatah", R.drawable.ic_uin, 0.7f),
    DummyMenuIcon("IAIN SAS\nBABEL", R.drawable.ic_iain, 0.7f),
    DummyMenuIcon("Lainnya", R.drawable.ic_lainnya1, 0.6f, route = "lainnya_pajak"),
    )

    /** Dummy "Daftar Pembayaran Terakhir" di PajakPendidikanPage. */
    val pajakPaymentList = listOf(
    DummyPaymentMethod("SAMSAT", "1234560", R.drawable.ic_samsat),
    DummyPaymentMethod("Pajak Bumi & Bangunan", "1234567890", R.drawable.ic_pbb),
    DummyPaymentMethod("Universitas Sriwijaya", "1234567890", R.drawable.ic_unsri),
    DummyPaymentMethod("Universitas Muhammadiyah", "1234567890", R.drawable.ic_ump),
    DummyPaymentMethod("Universitas Sriwijaya", "1234567890", R.drawable.ic_unsri),
    DummyPaymentMethod("Universitas Tridinanti", "1834787534", R.drawable.ic_tridinanti),
    DummyPaymentMethod("Universitas Sriwijaya", "5874398578", R.drawable.ic_unsri),
    DummyPaymentMethod("Universitas Muhammadiyah", "123457367", R.drawable.ic_ump),
    )

    /** Dummy daftar lengkap kategori pajak & pendidikan utk LainnyaPajakPage (dgn search). */
    val pajakLainnyaMenuItems = listOf(
    DummyMenuIcon("SAMSAT", R.drawable.ic_samsat, 0.7f),
    DummyMenuIcon("Pajak Bumi dan Bangunan", R.drawable.ic_pbb, 0.75f),
    DummyMenuIcon("Universitas Sriwijaya", R.drawable.ic_unsri, 0.7f),
    DummyMenuIcon("Universitas Muhammadiyah Palembang", R.drawable.ic_ump, 0.7f),
    DummyMenuIcon("Universitas Tridinanti", R.drawable.ic_tridinanti, 0.7f),
    DummyMenuIcon("UIN Raden Fatah Palembang", R.drawable.ic_uin, 0.75f),
    DummyMenuIcon("Universitas PGRI Palembang", R.drawable.ic_pgri, 0.75f),
    DummyMenuIcon("STIKP Muhammadiyah Palembang", R.drawable.ic_stikp, 0.7f),
    DummyMenuIcon("STIK SITI Khadijah Palembang", R.drawable.ic_stik, 0.7f),
    DummyMenuIcon("IAIN SAS BABEL", R.drawable.ic_iain, 0.7f),
    DummyMenuIcon("Universitas Baturaja", R.drawable.ic_ub, 0.7f),
    DummyMenuIcon("Universitas Bina Darma", R.drawable.ic_ubd, 0.4f),
    DummyMenuIcon("Ikest Muhammadiyah Palembang", R.drawable.ic_ikest, 0.7f),
    )

    val tagihanMenuItems = listOf(
    DummyMenuIcon("PDAM/SP2J", R.drawable.ic_pdam, 0.65f),
    DummyMenuIcon("BPJS", R.drawable.ic_bpjs, 0.8f),
    DummyMenuIcon("Telkom", R.drawable.ic_telkom, 0.8f),
    DummyMenuIcon("MNC/\nIndovision", R.drawable.ic_mnc, 0.6f),
    DummyMenuIcon("Tokopedia", R.drawable.ic_tokopedia, 0.6f),
    DummyMenuIcon("Musi Banyuasin Electric Power", R.drawable.ic_musi, 0.6f),
    DummyMenuIcon("Gas Petro", R.drawable.ic_gas, 0.75f),
    DummyMenuIcon("Pusri", R.drawable.ic_pusri, 0.7f),
    )

    /** Dummy "Daftar Pembayaran Terakhir" di TagihanPage. */
    val tagihanPaymentList = listOf(
    DummyPaymentMethod("Tokopedia", "1234560", R.drawable.ic_tokopedia),
    DummyPaymentMethod("BPJS", "1738742343", R.drawable.ic_bpjs),
    DummyPaymentMethod("Pusri", "1234567890", R.drawable.ic_pusri),
    // Catatan: di Dart, item "Gas Petro" ini pakai assetPath 'ump.svg' (kemungkinan
    // salah tempel saat development) -- dipertahankan apa adanya (ic_ump) di sini.
    DummyPaymentMethod("Gas Petro", "1843725862", R.drawable.ic_ump),
    DummyPaymentMethod("Telkom", "83547587", R.drawable.ic_telkom),
    )

    val cardlessMenuItems = listOf(
    DummyMenuIcon("Setor Tunai", R.drawable.ic_cardless1, 0.6f),
    DummyMenuIcon("Tarik Tunai", R.drawable.ic_cardless1, 0.6f),
    )

    /** Dummy "Daftar Cardless Terakhir" di CardlessPage. */
    val cardlessHistoryList = listOf(
    DummyCardlessHistory("Tarik Tunai", "23 September 2025 - 12.00 WIB", "Kadaluwarsa"),
    DummyCardlessHistory("Tarik Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Kadaluwarsa"),
    DummyCardlessHistory("Tarik Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Berhasil"),
    DummyCardlessHistory("Setor Tunai", "23 September 2025 - 12.00 WIB", "Kadaluwarsa"),
    )
}