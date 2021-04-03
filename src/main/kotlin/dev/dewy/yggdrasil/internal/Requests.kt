package dev.dewy.yggdrasil.internal

internal data class AuthenticateRequest(val username: String, val password: String, val agent: AuthAgent)

internal data class SignOutRequest(val username: String, val password: String)

internal data class TokenRequest(val accessToken: String, val clientToken: String)
