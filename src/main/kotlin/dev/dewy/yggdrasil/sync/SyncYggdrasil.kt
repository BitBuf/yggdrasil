package dev.dewy.yggdrasil.sync

import dev.dewy.yggdrasil.Yggdrasil
import dev.dewy.yggdrasil.models.Game
import dev.dewy.yggdrasil.models.TokenPair
import kotlinx.coroutines.runBlocking
import java.util.UUID

object SyncYggdrasil {
    @JvmStatic
    fun authenticate(username: String, password: String, game: Game, uuid: UUID = UUID.randomUUID()): TokenPair {
        return runBlocking {
            Yggdrasil.authenticate(username, password, game, uuid)
        }
    }

    @JvmStatic
    fun refresh(pair: TokenPair): TokenPair {
        return runBlocking {
            Yggdrasil.refresh(pair)
        }
    }

    @JvmStatic
    fun validate(pair: TokenPair): Boolean {
        return runBlocking {
            Yggdrasil.validate(pair)
        }
    }

    @JvmStatic
    fun signOut(username: String, password: String) {
        runBlocking {
            Yggdrasil.signOut(username, password)
        }
    }

    @JvmStatic
    fun invalidate(pair: TokenPair) {
        runBlocking {
            Yggdrasil.invalidate(pair)
        }
    }
}