package bsb.dev.bsb_bangking_jp.core.dummy
import androidx.annotation.DrawableRes
import bsb.dev.bsb_bangking_jp.R
import bsb.dev.bsb_bangking_jp.core.util.InitialName

data class DummyProfile(
    val nama: String,
    val photoBytes: ByteArray? = null,
    val noHp: String,
)

data class DummyRekening(
    val name: String,
    val number: String,
    val saldo: String,
    val isPrimary: Boolean,
)

data class DummyTransaksi(
    val jenis: String,
    val detail: String,
    val nominal: String,
    val isMasuk: Boolean,
    val tanggal: String, // "02 Jul 2026"
)

data class Dummymessage(
    val title: String,
    val subtitle: String,
    val amount: String,
    val status: String, // "Berhasil" / "Gagal" / "Pending"
    val tanggal: String,
)



data class DummyBerita(
    val title: String,
    @DrawableRes
    val imageRes: Int,
)

/** Ditambahkan sebagai padanan dummy untuk GetBannerBloc (BannerPemberitahuan). */
data class DummyBanner(
    val message: String,
)

/** Dummy untuk 1 item di tab "Transfer Terakhir" pada TransferPage. */
data class DummyLastTransfer(
    val id: String,
    val nama: String,
    val bank: String,
    val accountNumber: String,
)

/** Dummy untuk 1 item di tab "Daftar Tersimpan" pada TransferPage. */
data class DummySavedRecipient(
    val id: String,
    val alias: String,
    val bankName: String,
    val accountNumber: String,
)

/** Dummy untuk 1 item bank di PilihBankSheet (padanan BankItem/daftar_bank_model). */
data class DummyBank(
    val bankCode: String,
    val bankName: String,
)

/**
 * Dummy untuk hasil "inquiry" rekening tujuan (padanan TransferInquiryModel).
 * `isOnUs = true` artinya rekening sesama bank (bankCode "120"), dipakai
 * TransferBaruPage/DetailRekeningBaruModal untuk menentukan halaman berikutnya
 * (TransferBSBPage vs TransferUmumPage).
 */
data class DummyTransferInquiry(
    val isOnUs: Boolean,
    val bankCode: String,
    val bankName: String,
    val beneficiaryAccountNo: String,
    val beneficiaryName: String,
    val initials: String,
)

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
    val profile = DummyProfile(
        nama = "Angelica Regina Irvan",
        noHp= "0812xxxx5678"
    )

    val rekeningList = listOf(
        DummyRekening("Angelica Regina Irvan", "1606200435", "Rp 120.500.000", true),
        DummyRekening("Angelica Regina Irvan", "1213141516", "Rp 88.888.888", false),
    )

    val transaksiList = listOf(
        DummyTransaksi("Transfer", "Transfer ke Budi Santoso", "+ Rp 500.000", true, "02 Jul 2026"),
        DummyTransaksi("Tagihan", "Bayar Listrik PLN", "- Rp 250.000", false, "02 Jul 2026"),
        DummyTransaksi("Top Up", "Top Up GoPay", "- Rp 100.000", false, "01 Jul 2026"),
        DummyTransaksi("Transfer", "Terima dari Siti Aminah", "+ Rp 1.200.000", true, "30 Jun 2026"),
        DummyTransaksi("Tagihan", "Bayar Internet Indihome", "- Rp 350.000", false, "29 Jun 2026"),
    )

    val messageList = listOf(
        Dummymessage("Transfer ke Budi Santoso", "Transfer berhasil diproses", "- Rp 500.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Bayar Listrik PLN", "Pembayaran tagihan berhasil", "- Rp 250.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Top Up GoPay", "Transaksi sedang diproses", "- Rp 100.000", "Pending", "01 Jul 2026"),
        Dummymessage("Transfer ke Budi Santoso", "Transfer berhasil diproses", "- Rp 500.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Bayar Listrik PLN", "Pembayaran tagihan berhasil", "- Rp 250.000", "Gagal", "02 Jul 2026"),
        Dummymessage("Top Up GoPay", "Transaksi sedang diproses", "- Rp 100.000", "Pending", "01 Jul 2026"),
        Dummymessage("Transfer ke Budi Santoso", "Transfer berhasil diproses", "- Rp 500.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Bayar Listrik PLN", "Pembayaran tagihan berhasil", "- Rp 250.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Top Up GoPay", "Transaksi sedang diproses", "- Rp 100.000", "Pending", "01 Jul 2026"),
        Dummymessage("Transfer ke Budi Santoso", "Transfer berhasil diproses", "- Rp 500.000", "Berhasil", "02 Jul 2026"),
        Dummymessage("Bayar Listrik PLN", "Pembayaran tagihan berhasil", "- Rp 250.000", "Gagal", "02 Jul 2026"),
        Dummymessage("Top Up GoPay", "Transaksi sedang diproses", "- Rp 100.000", "Pending", "01 Jul 2026"),
    )

    val beritaList = listOf(
        DummyBerita(
            title = "Promo Cashback 20% untuk Transaksi QRIS",
            imageRes = R.drawable.berita1
        ),
        DummyBerita(
            title = "Bank Sumsel Babel Luncurkan Fitur Baru",
            imageRes = R.drawable.berita2
        ),
        DummyBerita(
            title = "Tips Aman Bertransaksi Online",
            imageRes = R.drawable.berita3
        )
    )

    val bannerList = listOf(
        DummyBanner("Jangan pernah membagikan PIN kepada siapapun."),
    )

    /** Dummy untuk tab "Transfer Terakhir" di TransferPage. */
    val lastTransferList = listOf(
        DummyLastTransfer("lt1", "Budi Santoso", "Bank Sumsel Babel", "1234567890"),
        DummyLastTransfer("lt2", "Siti Aminah", "BCA", "0987654321"),
        DummyLastTransfer("lt3", "Andi Wijaya", "Bank Mandiri", "1122334455"),
        DummyLastTransfer("lt4", "Rina Kartika", "BNI", "5566778899"),
        DummyLastTransfer("lt5", "Dedi Prasetyo", "BRI", "6677889900"),
    )

    /** Dummy untuk tab "Daftar Tersimpan" di TransferPage. */
    val savedRecipientList = listOf(
        DummySavedRecipient("sr1", "Budi (Kantor)", "Bank Sumsel Babel", "1234567890"),
        DummySavedRecipient("sr2", "Ibu Siti", "BCA", "0987654321"),
        DummySavedRecipient("sr3", "Andi W", "Bank Mandiri", "1122334455"),
        DummySavedRecipient("sr4", "Kos Rina", "BNI", "5566778899"),
    )

    /**
     * Dummy daftar bank untuk PilihBankSheet. bankCode "120" dianggap "Sesama Bank"
     * (padanan filter `bank.bankCode == "120"` di ), sisanya masuk "Daftar Bank Lain".
     */
    val bankList = listOf(
        DummyBank("120", "Bank Sumsel Babel"),
        DummyBank("008", "Bank Mandiri"),
        DummyBank("002", "Bank Rakyat Indonesia (BRI)"),
        DummyBank("009", "Bank Negara Indonesia (BNI)"),
        DummyBank("014", "Bank Central Asia (BCA)"),
        DummyBank("013", "Bank Permata"),
        DummyBank("022", "CIMB Niaga"),
        DummyBank("011", "Bank Danamon"),
        DummyBank("147", "Bank Muamalat"),
        DummyBank("451", "Bank Syariah Indonesia (BSI)"),
    )

    val paymentMethodList = listOf(
    DummyPaymentMethod("BSB Cash", "1234560", R.drawable.ic_bsb_cash1),
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

    private val dummyBeneficiaryNames = listOf(
    "Budi Santoso", "Siti Aminah", "Andi Wijaya", "Rina Kartika", "Dedi Prasetyo",
    )

    fun getDummyInquiry(
    bankCode: String,
    bankName: String,
    accountNumber: String,
    ): DummyTransferInquiry {
    val isOnUs = bankCode == "120"
    val nameIndex = if (accountNumber.isEmpty()) {
    0
    } else {
    accountNumber.sumOf { it.code } % dummyBeneficiaryNames.size
    }
    val beneficiaryName = dummyBeneficiaryNames[nameIndex]

    return DummyTransferInquiry(
    isOnUs = isOnUs,
    bankCode = bankCode,
    bankName = bankName,
    beneficiaryAccountNo = accountNumber,
    beneficiaryName = beneficiaryName,
    initials = beneficiaryName.InitialName(),
    )
    }
}