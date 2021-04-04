package dev.dewy.yggdrasil.extensions

import java.util.UUID

/**
 * Returns the *trimmed* version of this UUID as a string. That is, the kind without the dashes in.
 *
 * @return Trimmed UUID as a string.
 */
fun UUID.trimmed(): String {
    return this.toString().replace("-", "")
}