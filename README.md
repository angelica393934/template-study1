# MB Android



## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

* [Create](https://docs.gitlab.com/user/project/repository/web_editor/#create-a-file) or [upload](https://docs.gitlab.com/user/project/repository/web_editor/#upload-a-file) files
* [Add files using the command line](https://docs.gitlab.com/topics/git/add_files/#add-files-to-a-git-repository) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://gitlab.com/redesign-mobile-banking/mb-android.git
git branch -M main
git push -uf origin main
```
Now :
Android 8.0 Oreo
API Level 26

folder Redesign
folder dibangun berdasarkan feature-based, bukan layer-based (data/, domain/, presentation/ di root lalu isinya campur semua fitur)
pola yang sudah dipakai secara luas — namanya Package-by-Feature dengan Layered Sub-folder, dipakai di reference architecture Google (Now in Android), dan di banyak project Android skala besar.
├── data/          → urusan bicara ke server
├── domain/        → kontrak penghubung
├── presentation/  → logika tampilan

FEATURE- per fitur
data- Semua file yang berhubungan dengan API: endpoint, bentuk JSON, dan proses mengirim/menerima.
ApiService	: Daftar endpoint untuk request (@POST("v1/login")). Hanya definisi, tanpa logika. Kadang tidak ada kalau pakai GET-with-body (langsung lewat GetWithBodyApiHelper).
Models	: Mendefinisikan bentuk request (yang dikirim) dan response (yang diterima), plus mapper toDomain().
RepositoryImpl	: Otak utama. Menyusun header dan body, membuat signature, memanggil API, mengecek respCode, menyimpan token/cache, dan mengubah error jadi pesan ramah. Semua dikumpulkan di sini.

    domain- Lapisan penghubung antara ViewModel dan data. Isinya kontrak dan bentuk data bersih, sehingga ViewModel tidak tahu proses di baliknya (Retrofit, header, signature, dll). Bisa disebut lapisan abstraksi / kontrak.
        Repository (interface)	:Kontrak. daftar kemampuan yang boleh dipanggil ViewModel (login(...)). ViewModel hanya kenal ini, bukan implementasinya.
        Item / Model domain	:Bentuk data bersih untuk dipakai aplikasi, terpisah dari format JSON backend.
    
    presentation- Mengatur logika tampilan: state layar, validasi ringan, memutuskan kapan loading, error, atau pindah halaman.
        Contract	: Kosakata antara ViewModel dan UI: UiState (kondisi layar), NavEvent (perintah pindah halaman), UiEvent (toast dan efek sekali jalan).
        ViewModel	: Menerima aksi dari UI, validasi ringan, memanggil repository, lalu mengubah hasilnya menjadi UiState atau event.
    
    Navgraph.kt : daftar navigation
    Page        : code UI layar
    Module.kt   : daftar dependensi Koin.


CORE/ — fondasi umum yang dipakai semua fitur
core/components — komponen UI reusable
AppButton, AppTextField, AppCheckBox, AppSwitch, SearchTextField: elemen input dan tombol standar.
AppHeader: header biru dengan tombol back.
AppModalBottomSheet, AppModalConfirm, BottomSheetIndicator: bottom sheet dan dialog konfirmasi.
AppToast (ToastState, ToastHost) dan LoadingOverlay: toast dan overlay loading global lewat CompositionLocal.
OtpForm, OtpInput, InputPinPage, AppPinKeyboard: form OTP dan input PIN 6 digit dengan keyboard custom.
EmptyState, CustomRefreshIndicator, AdaptiveScrollColumn: state kosong, indikator pull-to-refresh, dan kolom yang bisa scroll di layar kecil.
AppMenu, AppMenuHorizontal, CardHistory, InitialAvatar, SelectableOptionCard, FilterChipBar, RuleBullet, TransactionDetailRow: komponen menu, kartu, avatar inisial, chip filter, dan baris detail transaksi.
SaldoCardBase, SaldoCardEmpty, RekeningLainnyaSheet: kartu saldo dan sheet pilih rekening.
BannerKeamanan: banner pengumuman yang bisa ditutup, dan tidak muncul lagi hari itu.
PilihTanggalSheet, PilihRentangTanggalSheet: kalender pilih tanggal dan rentang tanggal.

    core/theme — tema aplikasi
        Color: palet warna. 
        ExtendedColor: warna tambahan di luar Material.
        Theme: skema terang/gelap. 
        Type/Font: tipografi (Plus Jakarta Sans). 
        Spacing: konstanta jarak.

    core/network — lapisan jaringan
        header/
            ApiHeaders: pembuat header standar, yaitu timestamp, device-id, signature, dan lainnya.
        token/: 
            data/
                RefreshTokenApiService.kt ; endpoint refresh token.
                RefreshTokenModels.kt ; bentuk JSON refresh token request and respone.
                TokenRepositoryImpl.kt: otak proses refresh. Berisi konfigurasi tiap fase (cara simpan token, endpoint refresh) dan memakai Mutex agar hanya satu refresh berjalan sekaligus. Fase TRANSFER tidak bisa di-refresh karena tokennya sekali pakai. Kalau refresh LOGIN ditolak (4xx), sesi dianggap habis dan logout paksa dipicu.
            domain/
                TokenRepository.kt: interface dengan dua fungsi, getAccessToken(phase) dan refresh(phase, failedToken).
            TokenPhase.kt : pembungkus fase supaya bisa ditempel ke request Retrofit lewat @Tag.
            TokenRefreshInterceptor.kt: penghubung ke OkHttp. Memasang Authorization: Bearer, lalu kalau respons berisi respCode "0465" (token expired), memanggil refresh dan mengulang request sekali.
        NetworkModule (Koin): setup OkHttp dan Retrofit.
        NetworkConstants: base URL.
        ApiErrorResponse (ApiException, ApiErrorParser): parsing error dari backend.
        NetworkErrorMapper: mengubah exception jaringan jadi pesan ramah user.
        GetWithBodyHttpClient dan GetWithBodyApiHelper: client socket mentah untuk endpoint GET yang membawa body, karena OkHttp tidak mendukungnya.

    core/crypto — keamanan
        Ed25519KeyUtils: generate keypair Ed25519.
        SignatureUtils: membuat X-Signature dari body dan timestamp.
        JwtUtils: mengambil klaim challenge dari JWT.

    core/device — data perangkat dan penyimpanan lokal
        SecureStorageService: EncryptedSharedPreferences untuk private key dan semua token.
        AppPreferences: flag biasa (init sukses, remember me, banner ditutup, dan lainnya).
        DeviceContext: info perangkat (id, nama, OS, versi app).
        DeviceModule: registrasi Koin.

    core/session — sesi aplikasi
        ClearableRepository: interface untuk repository yang punya cache.
        SessionClearer: membersihkan semua cache saat logout.

    core/data — tempat menyimpan pengaturan tema dan bahasa
        datastore/
            AppPreference dan AppPreferenceRepository: menyimpan dark mode dan bahasa di DataStore.
        model/
            AppSettings: model pengaturannya tema dan bahasa

    core/viewmodel - untuk tema dan bahasa
        SettingsViewModel: mengelola tema dan bahasa.

    core/filter — fitur filter transaksi aktifitas dan pesan
        TransactionFilterPayload: model filter transaksi.
        FilterTransaksiModal: sheet filter jenis, kategori, dan tanggal.

    core/skeleton — placeholder loading ber-shimmer
        SkeletonBox (dasar), SkeletonAvatar, SkeletonList, SkeletonSaldoCard, SkeletonBerita, SkeletonMenu, SkeletonMenuUtama, SkeletonBanner, SkeletonRekeningCard, HaloUserSkeleton, ProfileSettingSkeleton, SkeletonDetailTransferTerjadwal.

    core/notification
        NotificationHelper: channel dan tampilan notifikasi lokal.
        AppFirebaseMessagingService: menerima push FCM.
        FcmTokenHelper: mengambil token FCM.

    core/util — fungsi bantu
        ApiResult: wrapper hasil API.
        RetryHelper: retry dengan delay.
        PinValidator: aturan M-PIN (tidak berurutan, tidak berulang).
        RupiahFormat, MaskAccountNumber, MaskPhoneNumber, InitialName: formatter dan penyamar data.
        DateFormatterUtil, BackendDateTimeUtil, DefaultRangeDate: format dan konversi tanggal.
        TransactionFilterChipMapper: filter menjadi chip.
        SystemSetting, GetAppVersion, ResponsiceScreen: info sistem, versi, dan ukuran responsif.

    core/dummy
        DummyData: data sementara untuk halaman yang backend-nya belum ada (top up, VA, tagihan, dan lainnya)

shared/ — fitur lintas halaman (punya data, repository, dan ViewModel sendiri)
shared/profile — profil user (nama, nomor HP) dan foto profilnya, lengkap dengan cache dan ViewModel single. ProfilePhotoPathUtils mengekstrak path foto.

    shared/rekening_lainnya — daftar rekening user, saldo, dan pengaturan rekening utama. cashBalanceValue() mengambil saldo tunai.
    
    shared/get_image — pengambil gambar dari server dengan autentikasi.
        ImageRepositoryImpl: cache di memori, batas 4 request paralel, dan penggabungan request yang sama.
        NetworkImage dan rememberNetworkImageState: composable untuk menampilkan gambar.
        ImageCategory: kategori path (berita, bank).
    
    shared/logout — API logout, LogoutViewModel, dan LogoutConfirmSheet.
    
    shared/session — sesi aplikasi
        SessionManager: menghapus token dan cache, lalu mengubah status login.g
        SessionGuard: logout paksa saat sesi habis. Timer idle dan background-nya sengaja dinonaktifkan.
        SessionExpiredNotifier: jembatan dari lapisan network ke SessionGuard.
        SessionExpiredDialog: dialog “Sesi Anda Telah Berakhir”.
    
    shared/transaction_result — hasil transaksi
        TransactionResultInfo: model hasil transaksi generik.
        TransactionResultPage: halaman bukti transaksi (berhasil, gagal, atau pending) dengan tombol simpan dan bagikan screenshot.