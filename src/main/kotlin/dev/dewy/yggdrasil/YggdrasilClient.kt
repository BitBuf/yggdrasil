package dev.dewy.yggdrasil

import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.InvalidCredentialsException
import dev.dewy.yggdrasil.models.TokenPair
import java.util.UUID

/**
 * Asynchronous Yggdrasil "client" and higher level interface for interacting with Yggdrasil.
 *
 * @author dewy
 */
class YggdrasilClient(
    val username: String,
    val password: String
) {
    var tokenPair: TokenPair? = null

    /**
     * Sign into Yggdrasil with the username and password you've set.
     *
     * @param game The [Game] you're trying to authenticate with; Minecraft by default.
     * @param clientIdentifier You can use a pre-existing client identifier as well.
     *
     * @author dewy
     */
    suspend fun signIn(game: Game = Game.MINECRAFT, clientIdentifier: UUID = UUID.randomUUID()) {
        tokenPair = Yggdrasil.authenticate(username, password, game, clientIdentifier)
    }

    /**
     * Refreshes an expired [tokenPair]'s access token for use.
     *
     * @author dewy
     */
    suspend fun refresh() {
        tokenPair?.let {
            tokenPair = Yggdrasil.refresh(it)
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to refresh(): token pair is null in YggdrasilClient.")
        }
    }

    /**
     * Returns whether or not this [YggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @return Whether or not this [YggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @author dewy
     */
    suspend fun isUsable(): Boolean {
        tokenPair?.let { pair ->
            if (pair.accessToken != null) {
                return Yggdrasil.validate(pair)
            } else {
                throw InvalidCredentialsException("Unable to determine isUsable(): access token is null in YggdrasilClient's token pair.")
            }
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to determine isUsable(): token pair is null in YggdrasilClient.")
        }
    }


    /**
     * Invalidate this account's most recent [TokenPair].
     *
     * @author dewy
     */
    suspend fun signOutWithPass() {
        Yggdrasil.signOut(username, password)

        tokenPair?.let {
            it.accessToken = null
        }
    }

    /**
     * Invalidate this [YggdrasilClient]'s current [TokenPair].
     *
     * @author dewy
     */
    suspend fun signOutWithToken() {
        tokenPair?.let { pair ->
            if (pair.accessToken != null) {
                Yggdrasil.invalidate(pair)

                pair.accessToken = null
            } else {
                throw InvalidCredentialsException("Unable to signOutWithToken(): access token is null in YggdrasilClient's token pair.")
            }
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to signOutWithToken(): token pair is null in YggdrasilClient.")
        }
    }
}
