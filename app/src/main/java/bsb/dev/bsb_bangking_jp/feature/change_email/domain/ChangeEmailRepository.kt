package bsb.dev.bsb_bangking_jp.feature.ganti_email.domain

interface GantiEmailRepository {
    /** Padanan GantiEmailService.GantiEmail() -- PUT /v1/dashboard/changeemail. */
    suspend fun gantiEmail(newEmail: String): Result<Unit>

    /** Padanan GantiEmailService.confirmGantiEmail() -- POST /v1/dashboard/confirmchangeemail. */
    suspend fun confirmGantiEmail(pin: String): Result<Unit>
}