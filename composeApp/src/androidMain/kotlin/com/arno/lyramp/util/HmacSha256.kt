package com.arno.lyramp.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual object HmacSha256 {
        actual fun compute(key: String, message: String): ByteArray {
                val mac = Mac.getInstance("HmacSHA256")
                val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
                mac.init(secretKey)
                return mac.doFinal(message.toByteArray(Charsets.UTF_8))
        }
}