package dev.dewy.yggdrasil.tests

import dev.dewy.yggdrasil.sync.SyncYggdrasil.authenticate
import dev.dewy.yggdrasil.sync.SyncYggdrasilClient

const val SYNC_USERNAME = "username (email, IGN for unmigrated accounts)"
const val SYNC_PASSWORD = "password"

/**
 * A simple example of how to use the synchronous **yggdrasil** library in Kotlin.
 * It's generally recommended you use the async way of doing things when using Kotlin; see **AsyncExample**.
 *
 * @author dewy
 */
fun main() {
    // Game.MINECRAFT and UUID.randomUuid() are the default parameters after *password* here.
    val pair = authenticate(SYNC_USERNAME, SYNC_PASSWORD)

    // It's recommended you use an YggdrasilClient instead of making more low-level Yggdrasil.whatever() requests yourself.
    val client = SyncYggdrasilClient(SYNC_USERNAME, SYNC_PASSWORD)

    println(pair)
    client.tokenPair = pair

    client.refresh()
    println(client.tokenPair)
}
