package dev.dewy.yggdrasil.sync

import dev.dewy.yggdrasil.Yggdrasil
import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.TokenPair
import kotlinx.coroutines.runBlocking
import java.util.UUID

/**
 * A synchronous wrapper around [Yggdrasil], to allow for Java interop that won't result in depression.
 * It's recommended to use [SyncYggdrasilClient] to interface with Yggdrasil. This class is basically low-level, raw Yggdrasil.
 *
 * Yeah, I hate this as well.
 *
 * @author dewy
 */
object SyncYggdrasil {
    /**
     * @see Yggdrasil.authenticate
     */
    @JvmStatic
    fun authenticate(username: String, password: String, game: Game = Game.MINECRAFT, uuid: UUID = UUID.randomUUID()): TokenPair {
        return runBlocking {
            Yggdrasil.authenticate(username, password, game, uuid)
        }
    }

    /**
     * @see Yggdrasil.refresh
     */
    @JvmStatic
    fun refresh(pair: TokenPair): TokenPair {
        return runBlocking {
            Yggdrasil.refresh(pair)
        }
    }

    /**
     * @see Yggdrasil.validate
     */
    @JvmStatic
    fun validate(pair: TokenPair): Boolean {
        return runBlocking {
            Yggdrasil.validate(pair)
        }
    }

    /**
     * @see Yggdrasil.signOut
     */
    @JvmStatic
    fun signOut(username: String, password: String) {
        runBlocking {
            Yggdrasil.signOut(username, password)
        }
    }

    /**
     * @see Yggdrasil.invalidate
     */
    @JvmStatic
    fun invalidate(pair: TokenPair) {
        runBlocking {
            Yggdrasil.invalidate(pair)
        }
    }
}
