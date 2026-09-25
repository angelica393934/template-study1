package bsb.dev.bsb_bangking_jp.shared.get_image.domain

interface ImageRepository {
    /**
     * @param path path mentah dari response API, mis. "/v1/image/xxx.jpeg" -- yang
     *   dipakai cuma NAMA FILE-nya saja (bagian setelah "/" terakhir).
     * @param category segmen path pengganti prefix lama, TERGANTUNG jenis gambar yang
     *   sedang diminta (berita = "image", banner = "banner", dst). Wajib diisi pemanggil
     *   agar tidak ada asumsi hardcode di sini.
     */
    suspend fun getImage(path: String?, category: ImageCategory): ByteArray?
}

/**
 * Daftar kategori gambar yang didukung endpoint getidimage. Tambah entri baru di sini
 * kalau ada jenis gambar baru -- TIDAK PERNAH mengubah logic ImageRepositoryImpl.
 */
enum class ImageCategory(val segment: String) {
    NEWS("banner"),
    BANK("bank"),
    // PROFILE("profile"), BANK_LOGO("bank"), dst -- tambah sesuai kebutuhan backend
}