package bsb.dev.bsb_bangking_jp.feature.pengaturan.dummy


/** Bullet item -- mendukung 1 level sub-bullet (padanan `List<dynamic>` di BulletList Dart). */
data class DummyFaqBullet(
    val text: String,
    val subItems: List<String> = emptyList(),
)

/** Jawaban FAQ -- bisa paragraf biasa (`text`), bullet list (`bullets`), atau gabungan keduanya. */
data class DummyFaqAnswer(
    val text: String? = null,
    val bullets: List<DummyFaqBullet> = emptyList(),
)

data class DummyFaqItem(
    val question: String,
    val answer: DummyFaqAnswer,
)

/**
 * Satu section Syarat & Ketentuan -- `content` multi-baris, diparse dengan aturan yang sama
 * seperti versi Dart (baris berawalan angka -> numbered row, baris berawalan "•" -> bullet indented).
 */
data class DummySyaratKetentuanSection(
    val title: String,
    val content: String,
)

data class DummyTentangAplikasi(
    val description: String,
    val featuresTitle: String,
    val features: List<String>,
    val closingText: String,
    val playStoreUrl: String,
    val appStoreUrl: String,
)

object DummyPengaturanData {

    val faqList = listOf(
        DummyFaqItem(
            question = "Apa Itu Mobile Banking BSB?",
            answer = DummyFaqAnswer(
                text = "Bank Sumsel Babel Mobile Merupakan Layanan Mobile Banking Dari PT Bank Pembangunan Daerah Sumatera Selatan Dan Bangka Belitung (Bank Sumsel Babel) Yang Dapat Diakses Langsung Oleh Nasabah Bank Sumsel Babel Melalui Aplikasi Pada HP Atau Tablet Berbasis Android Dan IOS Menggunakan Jaringan Internet Untuk Melakukan Transaksi Finansial Dan Non-Finansial."
            ),
        ),
        DummyFaqItem(
            question = "Transaksi Apa Saja Yang Dapat Dilakukan Melalui Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                text = "Nasabah Dapat Melakukan Transaksi Finansial Dan Non Finansial Melalui Bank Sumsel Babel Mobile Diantaranya: ",
                bullets = listOf(
                    DummyFaqBullet("Informasi Saldo."),
                    DummyFaqBullet("Hubungi Customer Service terdekat."),
                    DummyFaqBullet("Transfer ke Sesama Bank Sumsel Babel."),
                    DummyFaqBullet("Transfer Antar Bank."),
                    DummyFaqBullet("Transfer sesama Bank Sumsel Babel melalui QR Code Pembayaran."),
                    DummyFaqBullet("Pembayaran Merchant melalui QR Code Pembelian."),
                    DummyFaqBullet("Cek saldo Uang Elektronik BSB Cash."),
                    DummyFaqBullet("Cek Riwayat Transaksi Uang Elektronik BSB Cash."),
                    DummyFaqBullet("Top Up Uang Elektronik BSB Cash."),
                    DummyFaqBullet("Informasi Kurs."),
                    DummyFaqBullet("Informasi produk."),
                    DummyFaqBullet("Informasi Berita Lainnya."),
                ),
            ),
        ),
        DummyFaqItem(
            question = "HP Jenis Apa Saja Yang Support Untuk Menggunakan Aplikasi Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                text = "Perangkat Yang Support Untuk Aplikasi Bank Sumsel Babel Mobile Adalah Smartphone:",
                bullets = listOf(
                    DummyFaqBullet("Android dengan minimum OS versi 4.4."),
                    DummyFaqBullet("IOS minimum versi 9.0."),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Siapa Yang Dapat Menggunakan Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                text = "Merupakan Pemilik Rekening Tabungan Atau Giro Perorangan Di Bank Sumsel Babel Yang Berusia Minimal 12 Tahun."
            ),
        ),
        DummyFaqItem(
            question = "Bagaimana Cara Agar Dapat Membuat Akun Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                bullets = listOf(
                    DummyFaqBullet("Mendaftar layanan Fasilitas Bank Sumsel Babel Mobile di Kantor Cabang Bank Sumsel Babel terdekat. Siapkan buku tabungan, kartu ATM/ Debit dan e-KTP."),
                    DummyFaqBullet("Download aplikasi Bank Sumsel Babel Mobile melalui Play Store untuk HP berbasis android, atau melalui App Store untuk HP berbasis IOS."),
                    DummyFaqBullet("Bagi pengguna HP Dual SIM, pastikan nomor HP yang didaftarkan terpasang pada slot SIM utama."),
                    DummyFaqBullet("Lakukan aktivasi mandiri melalui aplikasi."),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Bagaimana Cara Melakukan Aktivasi Akun Pada Aplikasi Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                bullets = listOf(
                    DummyFaqBullet("Klik menu Aktivasi pada halaman utama aplikasi."),
                    DummyFaqBullet("Input nomor rekening atau nomor kartu ATM/ Debit yang terdaftar."),
                    DummyFaqBullet("Buat 8 karakter ID Pengguna Baru dan Kata Sandi Baru pada aplikasi mengunakan kombinasi huruf besar (A-Z), huruf kecil (a-z) dan angka (0-9)."),
                    DummyFaqBullet("Buat 6 digit angka (0-9) MPIN."),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Apa Fungsi Masing-Masing Dari ID Pengguna, Kata Sandi Dan MPIN??",
            answer = DummyFaqAnswer(
                bullets = listOf(
                    DummyFaqBullet("ID Pengguna digunakan untuk masuk/ login ke aplikasi."),
                    DummyFaqBullet("MPIN berfungsi sebagai nomor identifikasi pribadi pengguna aplikasi yang bersifat rahasia dan hanya di ketahui oleh pengguna yang diperlukan saat melakukan konfirmasi transaksi melalui aplikasi. Untuk keamanan pengguna, aplikasi akan diblokir ketika terjadi kesalahan Input MPIN sebanyak 3 kali."),
                    DummyFaqBullet(
                        text = "Tips terkait dengan Kata Sandi dan MPIN:",
                        subItems = listOf(
                            "Rahasiakan data pribadi berupa Kata Sandi dan MPIN kepada pihak lain termasuk kepada petugas bank.",
                            "Ganti Kata Sandi dan MPIN secara berkala.",
                        ),
                    ),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Bagaimana Jika Nasabah Lupa Kata Sandi (Password) Dan ID Pengguna Aplikasi Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                text = "Nasabah Dapat Menggunakan Fasilitas Lupa Password Dan Lupa ID Pengguna Pada Halaman Login/ Masuk Aplikasi Bank Sumsel Babel Mobile."
            ),
        ),
        DummyFaqItem(
            question = "Apakah MPIN Dapat Diubah? Bagaimana Caranya?",
            answer = DummyFaqAnswer(
                bullets = listOf(
                    DummyFaqBullet(
                        "Jika perubahan MPIN dilakukan atas kehendak nasabah (bukan karena lupa), maka perubahan MPIN dapat dilakukan melalui fitur ubah MPIN pada aplikasi Bank Sumsel Babel Mobile dengan memasukan MPIN lama untuk proses verifikasi, dan konfirmasi MPIN baru."
                    ),
                    DummyFaqBullet(
                        "Namun jika perubahan/reset MPIN dilakukan karena lupa, maka perubahan reset MPIN dapat dilakukan melalui Customer Service di Kantor Cabang/Capem/Kas terdekat."
                    ),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Jika Pulsa Pada HP Yang Saya Gunakan Habis, Apakah Saya Masih Dapat Menggunakan Aplikasi Bank Sumsel Babel Mobile?",
            answer = DummyFaqAnswer(
                text = "Ya. Aplikasi Bank Sumsel Babel Mobile Menggunakan Kuota Internet Sehingga Tetap Dapat Digunakan Walaupun Pulsa Habis."
            ),
        ),
        DummyFaqItem(
            question = "Pada Saat Melakukan Transaksi Menggunakan Aplikasi Bank Sumsel Babel Mobile, Mengapa Saya Tidak Menerima SMS OTP?",
            answer = DummyFaqAnswer(
                bullets = listOf(
                    DummyFaqBullet("Pastikan sinyal operator tidak mengalami gangguan untuk menerima SMS."),
                    DummyFaqBullet("Pastikan nomor HP anda telah terdaftar pada sistem Bank Sumsel Babel."),
                ),
            ),
        ),
        DummyFaqItem(
            question = "Apa Yang Harus Saya Lakukan Jika HP Saya Hilang Agar Aplikasi Bank Sumsel Babel Mobile Saya Tidak Disalahgunakan Orang-Orang Yang Tidak Bertanggung Jawab?",
            answer = DummyFaqAnswer(
                text = "Segera Blokir Akun Bank Sumsel Babel Mobile Anda Melalui Call Center 1500711 Atau Kantor Cabang Bank Sumsel Babel Terdekat."
            ),
        ),
        DummyFaqItem(
            question = "Mengapa Saya Tidak Dapat Melakukan Transaksi Melalui Aplikasi Bank Sumsel Babel Mobile Setelah Saya Salah Memasukkan MPIN Sebanyak 3 Kali?",
            answer = DummyFaqAnswer(
                text = "Untuk Keamanan Anda, Sistem Bank Sumsel Babel Akan Memblokir Akun Bank Sumsel Babel Mobile Anda Secara Otomatis Jika Terjadi Kesalahan Input MPIN Sebanyak 3 Kali."
            ),
        ),
        DummyFaqItem(
            question = "Bagaimana Cara Membuka Blokir Aplikasi Bank Sumsel Babel Mobile Saya Baik Karena Kesalahan Input MPIN Maupun Blokir Atas Permintaan Saya Sendiri?",
            answer = DummyFaqAnswer(
                text = "Untuk Membuka Blokir/ Mengaktifkan Kembali Akun Bank Sumsel Babel Mobile, Anda Dapat Mendatangi Customer Service Di Kantor Cabang/ Capem/ Kas Bank Sumsel Babel Terdekat."
            ),
        ),
    )

    val syaratKetentuanList = listOf(
        DummySyaratKetentuanSection(
            title = "A. DEFINISI",
            content = """
                1. Bank Sumsel Babel Mobile merupakan layanan Mobile Banking dari PT Bank Pembangunan Daerah Sumatera Selatan dan Bangka Belitung (Bank Sumsel Babel) yang dapat diakses langsung oleh nasabah Bank Sumsel Babel melalui aplikasi pada HP menggunakan jaringan internet sesuai ketentuan yang berlaku di Bank Sumsel Babel.
                2. Nasabah adalah pemilik rekening di Bank Sumsel Babel yang data pribadinya tercatat dalam sistem pencatatan Bank Sumsel Babel.
                3. ID Pengguna/Username adalah 8 karakter kombinasi angka (0-9) dan huruf yang terdiri dari huruf kecil (a-z) dan huruf kapital (A-Z) yang dibuat oleh nasabah secara mandiri melalui aplikasi pada saat pengaktifan layanan Bank Sumsel Babel Mobile, yang diperlukan nasabah untuk masuk/login ke aplikasi.
                4. Kata Sandi/Password adalah 8 karakter kombinasi angka (0-9) dan huruf yang terdiri dari huruf kecil (a-z) dan huruf kapital (A-Z) yang dibuat oleh nasabah secara mandiri melalui aplikasi pada saat pengaktifan layanan Bank Sumsel Babel Mobile yang diperlukan oleh nasabah untuk konfirmasi masuk/login ke aplikasi.
                5. MPIN (Mobile Personal Identification Number) adalah 6 digit kombinasi angka (0-9) yang dibuat nasabah secara mandiri melalui aplikasi pada saat pengaktifan layanan Bank Sumsel Babel Mobile, dimana MPIN merupakan nomor identifikasi pribadi nasabah, bersifat rahasia dan hanya diketahui oleh nasabah, yang diperlukan oleh nasabah untuk melakukan transaksi melalui Bank Sumsel Babel Mobile, serta dapat diubah sewaktu-waktu oleh nasabah melalui aplikasi Mobile Banking Bank Sumsel Babel.
                6. OTP (One Time Password) adalah 6 digit kombinasi angka (0-9) yang dihasilkan secara acak oleh aplikasi Bank Sumsel Babel Mobile yang dikirim ke nomor HP nasabah melalui pesan singkat atau SMS dan digunakan sebagai otorisasi atas setiap transaksi layanan Mobile Banking yang dilakukan oleh nasabah.
                7. HP adalah perangkat komunikasi seluler dalam bentuk handphone atau telepon seluler berbasis smartphone yang dipakai nasabah untuk mengakses aplikasi Bank Sumsel Babel Mobile, termasuk di dalamnya jaringan operator jasa seluler yang digunakan oleh nasabah.
                8. Nomor HP (SIM Card) adalah nomor telepon seluler nasabah yang terdaftar dan digunakan nasabah untuk bertransaksi menggunakan layanan Bank Sumsel Babel Mobile.
                9. Short Message Service (SMS) adalah sebuah layanan yang dilaksanakan dengan menggunakan HP untuk mengirim atau menerima pesan-pesan pendek.
                10. Registrasi adalah proses pendaftaran layanan Mobile Banking yang dilakukan nasabah melalui Customer Service dalam rangka pembukaan layanan Bank Sumsel Babel Mobile.
                11. Aktivasi adalah proses pengaktifan layanan Bank Sumsel Babel Mobile yang dilakukan oleh nasabah secara mandiri melalui aplikasi Bank Sumsel Babel Mobile pada HP dengan nomor HP (SIM Card) yang telah diregistrasi pada Customer Service.
                12. Android adalah sistem operasi berbasis Linux yang dikembangkan oleh Android Inc dengan dukungan dari Google untuk menjalankan HP layar sentuh (smartphone).
                13. IOS adalah sistem operasi untuk mobile yang dikembangkan oleh perusahaan Apple dan khusus untuk menjalankan perangkat iPhone, iPad, dan iPod yang diciptakan oleh Apple.
            """.trimIndent(),
        ),
        DummySyaratKetentuanSection(
            title = "B. REGISTRASI BANK SUMSEL BABEL MOBILE",
            content = """
                1. Kata Sandi dan MPIN hanya boleh digunakan oleh Nasabah yang bersangkutan.
                2. Nasabah wajib merahasiakan MPIN dan Kata Sandi dengan cara;
                • tidak memberitahukan MPIN dan Kata Sandi kepada orang lain termasuk kepada anggota keluarga atau orang terdekat Nasabah;
                • tidak menyimpan MPIN dan Kata Sandi pada HP, benda-benda lainnya atau sarana apapun lainnya yang memungkinkan MPIN dan Kata Sandi diketahui oleh orang lain;
                • berhati-hati dalam menggunakan MPIN dan Kata Sandi agar tidak terlihat oleh orang lain;
                • tidak menggunakan nomor HP, MPIN, dan Kata Sandi yang ditentukan atau dipilihkan oleh orang lain, atau yang mudah diterka seperti tanggal lahir atau kombinasinya dan nomor telepon.
                3. Untuk keamanan nasabah, maka;
                • OTP berlaku maksimum 5 menit;
                • nasabah tidak diperkenankan login dengan akun Bank Sumsel Babel Mobile yang sama pada HP berbeda; dan,
                • Nasabah dibatasi untuk input salah ID Pengguna, kata sandi, MPIN dan OTP maksimum sebanyak 3 (tiga) kali, jika telah melebihi batas maksimum kesalahan maka akun akan otomatis terblokir.
                4. Segala penyalahgunaan MPIN dan Kata Sandi merupakan tanggung jawab nasabah sepenuhnya.
                5. Penggunaan MPIN dan Kata Sandi pada aplikasi Bank Sumsel Babel Mobile mempunyai kekuatan hukum yang sama dengan perintah tertulis yang ditandatangani oleh nasabah.
                6. Nasabah setiap saat dapat mengubah MPIN dan Kata Sandi melalui aplikasi Bank Sumsel Babel Mobile.
                7. Apabila SIM Card atau HP milik nasabah hilang/dicuri, Nasabah harus memberitahukan hal tersebut kepada kantor Cabang/Cabang Pembantu/Kas Bank Sumsel Babel terdekat atau melalui Call Center 1500711 untuk dilakukan pemblokiran. Segala instruksi transaksi berdasarkan penggunaan nomor HP, MPIN dan Kata Sandi yang terjadi sebelum pemberitahuan dari nasabah kepada pihak Bank Sumsel Babel merupakan tanggung jawab Nasabah sepenuhnya.
            """.trimIndent(),
        ),
        DummySyaratKetentuanSection(
            title = "C. KETENTUAN PENGGUNAAN",
            content = """
                1. Proses aktivasi aplikasi Bank Sumsel Babel Mobile hanya dapat dilakukan pada HP dengan SIM Card yang terdaftar pada slot SIM utama/pertama pada HP (jika HP menggunakan dual SIM).
                2. Saat proses registrasi dan aktivasi, nasabah harus mempunyai pulsa yang mencukupi pada nomor HP terdaftar untuk biaya SMS, serta memiliki kuota dan/atau paket data internet.
                3. Satu nomor HP dapat didaftarkan untuk satu akun layanan Bank Sumsel Babel Mobile.
                4. Satu akun layanan Bank Sumsel Babel Mobile dapat digunakan untuk lebih dari satu rekening dengan pemilik yang sama.
                5. Untuk dapat bertransaksi menggunakan layanan Bank Sumsel Babel Mobile, nasabah harus:
                • Menggunakan HP berbasis teknologi smartphone merk dan tipe apa saja dengan menggunakan sistem operasi Android atau iOS
                • Menggunakan nomor HP/SIM Card yang dikeluarkan oleh operator-operator seluler mitra Bank Sumsel Babel dalam layanan Bank Sumsel Babel Mobile (Telkomsel, Indosat, XL, Smartfren, dan lain-lain)
                • Jalur komunikasi yang digunakan selama bertransaksi adalah menggunakan koneksi internet GPRS/EDGE/3G/4G/Wifi
                6. Pada saat bertransaksi menggunakan aplikasi Bank Sumsel Babel Mobile, nasabah harus mengisi semua data yang dibutuhkan untuk setiap transaksi secara benar dan lengkap.
                7. Sebagai tanda persetujuan, Nasabah wajib memasukkan MPIN dan OTP pada saat melakukan transaksi finansial dan transaksi lainnya yang ditentukan oleh Bank Sumsel Babel melalui aplikasi.
                8. Setiap instruksi dari Nasabah melalui aplikasi Bank Sumsel Babel Mobile yang tersimpan di dalam core Banking Bank Sumsel Babel merupakan data yang benar dan mengikat Nasabah, serta merupakan bukti yang sah atas instruksi dari Nasabah kepada Bank Sumsel Babel untuk melakukan transaksi yang dimaksud, kecuali Nasabah dapat membuktikan sebaliknya.
                9. Bank Sumsel Babel menerima dan menjalankan setiap instruksi dari Nasabah melalui aplikasi Bank Sumsel Babel Mobile sebagai instruksi yang sah berdasarkan penggunaan nomor HP, MPIN dan OTP. Bank Sumsel Babel tidak mempunyai kewajiban untuk meneliti atau menyelidiki keaslian maupun keabsahan atau kewenangan pengguna nomor HP, MPIN dan OTP atau menilai maupun membuktikan ketepatan maupun kelengkapan instruksi dimaksud, oleh karena itu instruksi tersebut adalah sah dan mengikat Nasabah secara hukum, kecuali Nasabah dapat membuktikan sebaliknya.
                10. Segala transaksi yang telah diinstruksikan oleh Nasabah kepada Bank Sumsel Babel melalui aplikasi Bank Sumsel Babel Mobile tidak dapat dibatalkan dengan alasan apapun.
                11. Bank Sumsel Babel berhak untuk tidak melaksanakan instruksi dari Nasabah melalui aplikasi Bank Sumsel Babel Mobile, jika saldo di rekening nasabah tidak mencukupi untuk melakukan transaksi yang bersangkutan atau rekening nasabah dalam kondisi diblokir.
                12. Nasabah wajib dan bertanggung jawab untuk memastikan ketepatan dan kelengkapan instruksi transaksi melalui aplikasi Bank Sumsel Babel Mobile. Segala akibat apapun yang timbul karena ketidaklengkapan, ketidakjelasan data, atau ketidaktepatan instruksi dari Nasabah merupakan tanggung jawab nasabah.
                13. Nasabah menyetujui dan mengakui keabsahan, kebenaran, atau keaslian bukti instruksi dan komunikasi yang dikirim secara elektronik oleh Bank Sumsel Babel, termasuk dokumen dalam bentuk catatan komputer atau bukti transaksi yang dijalankan oleh Bank Sumsel Babel, hasil print out komputer, salinan atau bentuk penyimpanan informasi yang lain yang terdapat pada Bank Sumsel Babel. Semua sarana dan/atau dokumen tersebut merupakan satu-satunya alat bukti yang sah dan mengikat atas transaksi-transaksi perbankan yang dilakukan oleh Nasabah melalui aplikasi Bank Sumsel Babel Mobile, kecuali Nasabah dapat membuktikan sebaliknya.
                14. Dengan melakukan transaksi melalui aplikasi Bank Sumsel Babel Mobile, Nasabah mengakui semua komunikasi dan instruksi dari Nasabah yang diterima Bank Sumsel Babel akan diperlakukan sebagai alat bukti yang sah meskipun dokumen tidak dibuat secara tertulis dan/atau dokumen tidak ditandatangani oleh Nasabah dan Bank Sumsel Babel.
                15. Bank Sumsel Babel memberlakukan limit transaksi harian untuk transaksi finansial melalui aplikasi Bank Sumsel Babel Mobile. Limit transaksi melalui Bank Sumsel Babel Mobile dapat dilihat melalui website https://banksumselbabel.com.
                16. Operator Seluler berhak mengenakan biaya kepada Nasabah untuk setiap transaksi, baik yang berhasil maupun yang tidak berhasil dilakukan.
                17. Kerugian yang ditimbulkan atas kesalahan dan/atau kelalaian nasabah atas penggunaan layanan Bank Sumsel Babel Mobile merupakan tanggung jawab nasabah.
                18. Kerugian yang setelah diinvestigasi merupakan kesalahan internal Bank maka Bank akan mengganti seluruh kerugian yang ditimbulkan.
                19. Nasabah wajib melakukan pengkinian data apabila terdapat pembaharuan terkait identitas Nasabah maupun Nomor Ponsel Nasabah dimaksud.
                20. Nasabah tidak diperkenankan untuk memindahtangankan penggunaan mobile banking ataupun rekening kepada pihak manapun.
            """.trimIndent(),
        ),
    )

    val tentangAplikasi = DummyTentangAplikasi(
        description = "Aplikasi ini dirancang untuk memberikan kemudahan dalam mengelola keuangan Anda. Dengan fitur-fitur yang lengkap dan antarmuka yang user-friendly, Anda dapat dengan mudah melacak pengeluaran, membuat anggaran, dan merencanakan keuangan masa depan Anda.",
        featuresTitle = "Fitur Utama:",
        features = listOf(
            "Pelacakan Pengeluaran: Catat semua pengeluaran harian Anda dengan mudah.",
            "Pembuatan Anggaran: Buat anggaran bulanan untuk membantu mengontrol pengeluaran.",
            "Laporan Keuangan: Dapatkan laporan keuangan yang detail untuk analisis lebih lanjut.",
            "Notifikasi Pengingat: Dapatkan notifikasi untuk tagihan dan pengingat penting lainnya.",
        ),
        closingText = "Kami berkomitmen untuk terus meningkatkan aplikasi ini berdasarkan masukan dari pengguna. Jika Anda memiliki saran atau pertanyaan, jangan ragu untuk menghubungi tim dukungan kami.",
        playStoreUrl = "https://play.google.com/store/apps/details?id=mlpt.siemo.mobilebankingbsb&pcampaignid=web_share",
        appStoreUrl = "https://apps.apple.com/id/app/mobile-bank-sumselbabel/id1473771097",
    )
}