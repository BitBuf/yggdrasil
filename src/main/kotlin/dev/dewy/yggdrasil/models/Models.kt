package dev.dewy.yggdrasil.models

/**
 * An *access token* and *client token* associated with one another.
 *
 * @param accessToken The access token. Y'know, the massive one.
 * @param clientToken The trimmed UUID associated with each Yggdrasil "client". Also referred to as a client identifier.
 *
 * @author dewy
 */
data class TokenPair(var accessToken: String?, val clientToken: String)

/**
 * The games that use Yggdrasil as their authentication system; Mojang's Minecraft and Scrolls.
 *
 * @param title The human readable game title.
 *
 * @author dewy
 */
enum class Game(val title: String) {
    MINECRAFT("Minecraft"),
    SCROLLS("Scrolls")
}
