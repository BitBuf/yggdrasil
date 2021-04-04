package dev.dewy.yggdrasil.models

/**
 * Thrown when any error response is received from the Yggdrasil server.
 *
 * @author dewy
 */
open class YggdrasilException(message: String) : Exception(message)

/**
 * Thrown when an invalid username / password combo or token is used.
 *
 * Keep in mind that *Yggdrasil.authenticate()* is extremely rate-limited, and too many login
 * attempts with a username / password combo in too short a time period will throw this exception, even if the combo is correct.
 *
 * @author dewy
 */
class InvalidCredentialsException(message: String) : YggdrasilException(message)
