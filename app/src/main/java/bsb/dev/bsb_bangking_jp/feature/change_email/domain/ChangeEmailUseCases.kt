package bsb.dev.bsb_bangking_jp.feature.ganti_email.domain

class GantiEmailUseCase(private val repository: GantiEmailRepository) {
    suspend operator fun invoke(newEmail: String): Result<Unit> = repository.gantiEmail(newEmail)
}

class ConfirmGantiEmailUseCase(private val repository: GantiEmailRepository) {
    suspend operator fun invoke(pin: String): Result<Unit> = repository.confirmGantiEmail(pin)
}