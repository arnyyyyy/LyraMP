package com.arno.lyramp.util

expect object HmacSha256 {
        fun compute(key: String, message: String): ByteArray
}