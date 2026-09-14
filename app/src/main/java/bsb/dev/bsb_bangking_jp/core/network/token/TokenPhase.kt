// core/network/token/TokenPhase.kt
package bsb.dev.bsb_bangking_jp.core.network.token

enum class TokenPhase {
    INIT,
    LOGIN,
    TRANSFER,
    REGIST,
    ACTIVATION,
    FORGET_ID_USER,
    FORGET_PW_USER,
}

data class TokenPhaseTag(val phase: TokenPhase)