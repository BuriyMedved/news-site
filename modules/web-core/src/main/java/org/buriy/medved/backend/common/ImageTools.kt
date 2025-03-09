package org.buriy.medved.backend.common

import java.util.*

fun imageBytesToBase64String(bytes: ByteArray): String {
    val encodeToString = Base64.getEncoder().encodeToString(bytes)
    return "data:image/png;base64, $encodeToString"
}