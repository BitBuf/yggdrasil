package dev.dewy.yggdrasil.sync

import dev.dewy.yggdrasil.YggdrasilClient
import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.InvalidCredentialsException
import dev.dewy.yggdrasil.models.TokenPair
import java.util.UUID

/**
 * Identical to the asynchronous [YggdrasilClient], except synchronous and using [SyncYggdrasil] as a backend.
 *
 * @author dewy
 */
class SyncYggdrasilClient(
    var username: String?,
    var password: String?
) {
    var tokenPair: TokenPair? = null

    constructor(pair: TokenPair, username: String? = null, password: String? = null) : this(username, password) {
        this.tokenPair = pair
    }

    /**
     * Sign into Yggdrasil with the username and password you've set.
     *
     * @param game The [Game] you're trying to authenticate with; Minecraft by default.
     * @param clientIdentifier You can use a pre-existing client identifier as well.
     *
     * @author dewy
     */
    fun signIn(game: Game = Game.MINECRAFT, clientIdentifier: UUID = UUID.randomUUID()) {
        if (username != null && password != null) {
            tokenPair = SyncYggdrasil.authenticate(username!!, password!!, game, clientIdentifier)
        } else {
            throw InvalidCredentialsException("Unable to signIn(): username or password is null in SyncYggdrasilClient.")
        }
    }

    /**
     * Refreshes an expired [tokenPair]'s access token for use.
     *
     * @author dewy
     */
    fun refresh() {
        tokenPair?.let {
            tokenPair = SyncYggdrasil.refresh(it)
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to refresh(): token pair is null in SyncYggdrasilClient.")
        }
    }

    /**
     * Returns whether or not this [SyncYggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @return Whether or not this [SyncYggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @author dewy
     */
    fun isUsable(): Boolean {
        tokenPair?.let { pair ->
            if (pair.accessToken != null) {
                return SyncYggdrasil.validate(pair)
            } else {
                throw InvalidCredentialsException("Unable to determine isUsable(): access token is null in SyncYggdrasilClient's token pair.")
            }
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to determine isUsable(): token pair is null in SyncYggdrasilClient.")
        }
    }

    /**
     * Invalidate this account's most recent [TokenPair].
     *
     * @author dewy
     */
    fun signOutWithPass() {
        if (username != null && password != null) {
            SyncYggdrasil.signOut(username!!, password!!)

            tokenPair?.let {
                it.accessToken = null
            }
        } else {
            throw InvalidCredentialsException("Unable to signOutWithPass(): username or password is null in SyncYggdrasilClient.")
        }
    }

    /**
     * Invalidate this [SyncYggdrasilClient]'s current [TokenPair].
     *
     * @author dewy
     */
    fun signOutWithToken() {
        tokenPair?.let { pair ->
            if (pair.accessToken != null) {
                SyncYggdrasil.invalidate(pair)

                pair.accessToken = null
            } else {
                throw InvalidCredentialsException("Unable to signOutWithToken(): access token is null in SyncYggdrasilClient's token pair.")
            }
        } ?: kotlin.run {
            throw InvalidCredentialsException("Unable to signOutWithToken(): token pair is null in SyncYggdrasilClient.")
        }
    }
}
