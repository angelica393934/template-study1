package bsb.dev.bsb_bangking_jp.feature.change_email.domain

class ChangeEmailUseCase(private val repository: ChangeEmailRepository) {
    suspend operator fun invoke(newEmail: String): Result<Unit> = repository.changeEmail(newEmail)
}

class ConfirmChangeEmailUseCase(private val repository: ChangeEmailRepository) {
    suspend operator fun invoke(pin: String): Result<Unit> = repository.confirmChangeEmail(pin)
}