//package com.arno.lyramp.feature.authorization.util
//
//import korlibs.crypto.SHA256
//import korlibs.crypto.encoding.Base64
//import kotlin.random.Random
//
//internal fun generateCodeVerifier(): String {
//        val bytes = ByteArray(64)
//        Random.nextBytes(bytes)
//        return Base64.encode(bytes, url = true)
//}
//
//internal fun String.toCodeChallengeS256(): String {
//        val digest = SHA256.digest(this.encodeToByteArray())
//        return Base64.encode(digest.bytes, url = true)
//}