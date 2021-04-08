package dev.dewy.yggdrasil.tests

import dev.dewy.yggdrasil.Yggdrasil.authenticate
import dev.dewy.yggdrasil.YggdrasilClient
import dev.dewy.yggdrasil.models.TokenPair
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val USERNAME = "username (email, IGN for unmigrated accounts)"
const val PASSWORD = "password"

/**
 * A simple example of how to use the **yggdrasil** library in Kotlin.
 *
 * @author dewy
 */
fun main() {
    var pair: TokenPair?

    // It's recommended you use an YggdrasilClient instead of making more low-level Yggdrasil.whatever() requests yourself.
    val client = YggdrasilClient(USERNAME, PASSWORD)

    GlobalScope.launch {
        // Game.MINECRAFT and UUID.randomUuid() are the default parameters after *password* here.
        pair = authenticate(USERNAME, PASSWORD)
        println(pair)

        client.tokenPair = pair

        client.refresh()
        println(client.tokenPair)
    }

    println("Coroutines moment!")

    Thread.sleep(3000L)
}
