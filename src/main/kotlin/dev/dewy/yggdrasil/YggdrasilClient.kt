package dev.dewy.yggdrasil

import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.TokenPair
import java.util.UUID

/**
 * Basic Yggdrasil "client" and higher level interface for interacting with Yggdrasil.
 *
 * @author dewy
 */
class YggdrasilClient {
    var username = ""
    var password = ""

    var accessToken = ""
    var clientIdentifier = ""

    /**
     * Sign into Yggdrasil with the username and password you've set.
     *
     * @param game The [Game] you're trying to authenticate with; Minecraft by default.
     * @param clientIdentifier You can use a pre-existing client identifier as well.
     *
     * @author dewy
     */
    suspend fun signIn(game: Game = Game.MINECRAFT, clientIdentifier: UUID = UUID.randomUUID()) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            assignNewPair(Yggdrasil.authenticate(username, password, game, clientIdentifier))
        }
    }

    /**
     * Refreshes your [accessToken] for use.
     *
     * @author dewy
     */
    suspend fun refresh() {
        if (accessToken.isNotEmpty() && clientIdentifier.isNotEmpty()) {
            val refreshedPair = Yggdrasil.refresh(TokenPair(accessToken, clientIdentifier))

            assignNewPair(refreshedPair)
        }
    }

    /**
     * Returns whether or not this [YggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @return Whether or not this [YggdrasilClient] is usable for authentication with a Minecraft server.
     *
     * @author dewy
     */
    suspend fun isUsable(): Boolean = Yggdrasil.validate(TokenPair(accessToken, clientIdentifier))

    /**
     * Invalidate all tokens in existence associated with this account.
     *
     * @author dewy
     */
    suspend fun signOutGlobally() {
        Yggdrasil.signOut(username, password)

        this.accessToken = ""
    }

    /**
     * Invalidate this [YggdrasilClient]'s current [accessToken].
     *
     * @author dewy
     */
    suspend fun signOut() {
        Yggdrasil.invalidate(TokenPair(accessToken, clientIdentifier))

        this.accessToken = ""
    }

    private fun assignNewPair(pair: TokenPair) {
        this.accessToken = pair.accessToken
        this.clientIdentifier = pair.clientToken
    }
}

/**
 * The [YggdrasilClient] builder.
 * [YggdrasilClient.signIn] is always ran when using this, so it's only recommended for brand new clients.
 *
 * @author dewy
 */
suspend fun yggdrasil(block: YggdrasilClient.() -> Unit): YggdrasilClient = YggdrasilClient().apply(block).apply {
    signIn()
}
