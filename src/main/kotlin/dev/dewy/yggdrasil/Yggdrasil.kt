package dev.dewy.yggdrasil

import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.dewy.yggdrasil.extensions.trimmed
import dev.dewy.yggdrasil.internal.AuthAgent
import dev.dewy.yggdrasil.internal.AuthenticateRequest
import dev.dewy.yggdrasil.internal.ErrorResponse
import dev.dewy.yggdrasil.internal.SignOutRequest
import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.TokenPair
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * A set of helper functions to manually make requests to Mojang's Yggdrasil server.
 *
 * @author dewy
 */
object Yggdrasil {
    private const val AUTH_SERVER = "https://authserver.mojang.com"

    private const val AUTHENTICATE = "$AUTH_SERVER/authenticate"
    private const val REFRESH = "$AUTH_SERVER/refresh"
    private const val VALIDATE = "$AUTH_SERVER/validate"
    private const val SIGN_OUT = "$AUTH_SERVER/signout"
    private const val INVALIDATE = "$AUTH_SERVER/invalidate"


    private val gson = Gson()


    /**
     * Authenticate with a Mojang username and password, and get a [TokenPair] back.
     *
     * @param username The Mojang account's "username" (email for migrated accounts, IGN for legacy accounts).
     * @param password The Mojang account's password.
     * @param clientToken You can specify a client token [UUID] to reuse. A randomly generated UUID by default.
     * @param game The [Game] you're trying to authenticate with; Minecraft by default.
     *
     * @return An access token and client identifier token, usable for authentication with any Mojang service.
     *
     * @author dewy
     */
    suspend fun authenticate(username: String, password: String, clientToken: UUID = UUID.randomUUID(), game: Game = Game.MINECRAFT): TokenPair = suspendCoroutine { cont ->
        AUTHENTICATE
            .httpPost()
            .jsonBody(
                AuthenticateRequest(AuthAgent(game.title, 1), username, password, clientToken.trimmed())
            )
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        cont.resume(extractTokenPair(result.get()))
                    }

                    is Result.Failure -> {
                        cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                    }
                }
            }
    }

    /**
     * Refreshes a valid access token, invalidating the original and returning a new one.
     * Note that the provided access token will be **invalidated**, with the newly returned [TokenPair]'s accessToken being usable.
     *
     * @param pair The token pair to refresh.
     *
     * @return Refreshed token pair, with an identical client token to the original [pair] but a different access token.
     *
     * @author dewy
     */
    suspend fun refresh(pair: TokenPair): TokenPair = suspendCoroutine { cont ->
        REFRESH
            .httpPost()
            .jsonBody(pair)
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        cont.resume(extractTokenPair(result.get()))
                    }

                    is Result.Failure -> {
                        cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                    }
                }
            }
    }

    /**
     * Returns whether or not an access token is usable for authentication with a Minecraft server.
     * If it returns false, it's recommended to [refresh] your [TokenPair].
     *
     * @param pair The token pair to validate.
     *
     * @return Whether or not an access token is usable for authentication with a Minecraft server.
     *
     * @author dewy
     */
    suspend fun validate(pair: TokenPair): Boolean = suspendCoroutine { cont ->
        VALIDATE
            .httpPost()
            .jsonBody(pair)
            .responseString { _, _, result ->
                cont.resume(result is Result.Success)
            }
    }

    /**
     * Invalidate all tokens associated with an account via its [username] and [password].
     *
     * @param username The Mojang account's "username" (email for migrated accounts, IGN for legacy accounts).
     * @param password The Mojang account's password.
     *
     * @author dewy
     */
    suspend fun signOut(username: String, password: String) = suspendCoroutine<Unit> { cont ->
        SIGN_OUT
            .httpPost()
            .jsonBody(
                SignOutRequest(username, password)
            )
            .responseString { _, _, result ->
                if (result is Result.Failure) {
                    cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                }

                cont.resume(Unit)
            }
    }

    /**
     * Invalidate a specific [TokenPair].
     *
     * @param pair The access token and client identifier token to invalidate.
     *
     * @author dewy
     */
    suspend fun invalidate(pair: TokenPair) = suspendCoroutine<Unit> { cont ->
        INVALIDATE
            .httpPost()
            .jsonBody(pair)
            .responseString { _, _, result ->
                if (result is Result.Failure) {
                    cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                }

                cont.resume(Unit)
            }
    }

    private fun getException(exception: String): YggdrasilException {
        val errorResponse = gson.fromJson(exception, ErrorResponse::class.java)

        return YggdrasilException("${errorResponse.error}: ${errorResponse.errorMessage}")
    }

    private fun extractTokenPair(fullResponse: String): TokenPair {
        val obj = gson.fromJson(fullResponse, JsonObject::class.java)

        return TokenPair(obj["accessToken"].asString, obj["clientToken"].asString)
    }
}