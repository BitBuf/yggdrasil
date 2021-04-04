package dev.dewy.yggdrasil.internal

internal data class AuthenticateRequest(val agent: AuthAgent, val username: String, val password: String, val clientToken: String)

internal data class SignOutRequest(val username: String, val password: String)
