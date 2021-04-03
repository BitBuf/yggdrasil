package dev.dewy.yggdrasil

import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.dewy.yggdrasil.internal.AuthAgent
import dev.dewy.yggdrasil.internal.AuthenticateRequest
import dev.dewy.yggdrasil.internal.ErrorResponse
import dev.dewy.yggdrasil.internal.SignOutRequest
import dev.dewy.yggdrasil.internal.TokenRequest
import dev.dewy.yggdrasil.models.Game
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Yggdrasil {
    private const val AUTH_SERVER = "https://authserver.mojang.com"

    private const val AUTHENTICATE = "$AUTH_SERVER/authenticate"
    private const val REFRESH = "$AUTH_SERVER/refresh"
    private const val VALIDATE = "$AUTH_SERVER/validate"
    private const val SIGN_OUT = "$AUTH_SERVER/signout"
    private const val INVALIDATE = "$AUTH_SERVER/invalidate"


    private val gson = Gson()


    suspend fun authenticate(username: String, password: String, game: Game): String = suspendCoroutine { cont ->
        AUTHENTICATE
            .httpPost()
            .jsonBody(
                AuthenticateRequest(username, password, AuthAgent(game.title, 1)), gson
            )
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        cont.resume(extractToken(result.get()))
                    }

                    is Result.Failure -> {
                        cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                    }
                }
            }
    }

    suspend fun refresh(accessToken: String, clientToken: String): String = suspendCoroutine { cont ->
        REFRESH
            .httpPost()
            .jsonBody(
                TokenRequest(accessToken, clientToken), gson
            )
            .responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        cont.resume(extractToken(result.get()))
                    }

                    is Result.Failure -> {
                        cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                    }
                }
            }
    }

    suspend fun validate(accessToken: String, clientToken: String) = suspendCoroutine<Unit> { cont ->
        VALIDATE
            .httpPost()
            .jsonBody(
                TokenRequest(accessToken, clientToken), gson
            )
            .responseString { _, _, result ->
                if (result is Result.Failure) {
                    cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                }

                cont.resume(Unit)
            }
    }

    suspend fun signOut(username: String, password: String) = suspendCoroutine<Unit> { cont ->
        SIGN_OUT
            .httpPost()
            .jsonBody(
                SignOutRequest(username, password), gson
            )
            .responseString { _, _, result ->
                if (result is Result.Failure) {
                    cont.resumeWithException(getException(result.getException().errorData.toString(Charsets.UTF_8)))
                }

                cont.resume(Unit)
            }
    }

    suspend fun invalidate(accessToken: String, clientToken: String) = suspendCoroutine<Unit> { cont ->
        INVALIDATE
            .httpPost()
            .jsonBody(
                TokenRequest(accessToken, clientToken), gson
            )
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

    private fun extractToken(fullResponse: String) = gson.fromJson(fullResponse, JsonObject::class.java)["accessToken"].asString
}