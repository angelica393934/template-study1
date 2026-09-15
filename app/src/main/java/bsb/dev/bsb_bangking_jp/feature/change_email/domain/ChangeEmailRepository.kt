package bsb.dev.bsb_bangking_jp.feature.change_email.domain

interface ChangeEmailRepository {
    /** Padanan ChangeEmailService.ChangeEmail() -- PUT /v1/dashboard/changeemail. */
    suspend fun changeEmail(newEmail: String): Result<Unit>

    /** Padanan ChangeEmailService.confirmChangeEmail() -- POST /v1/dashboard/confirmchangeemail. */
    suspend fun confirmChangeEmail(pin: String): Result<Unit>
}