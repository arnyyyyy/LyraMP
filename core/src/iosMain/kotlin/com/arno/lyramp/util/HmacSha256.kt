package com.arno.lyramp.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCHmac
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreCrypto.kCCHmacAlgSHA256

@OptIn(ExperimentalForeignApi::class)
actual object HmacSha256 {
        actual fun compute(key: String, message: String): ByteArray {
                val keyBytes = key.encodeToByteArray()
                val messageBytes = message.encodeToByteArray()
                val result = ByteArray(CC_SHA256_DIGEST_LENGTH)

                keyBytes.usePinned { keyPinned ->
                        messageBytes.usePinned { messagePinned ->
                                result.usePinned { resultPinned ->
                                        CCHmac(
                                                algorithm = kCCHmacAlgSHA256,
                                                key = keyPinned.addressOf(0),
                                                keyLength = keyBytes.size.convert(),
                                                data = messagePinned.addressOf(0),
                                                dataLength = messageBytes.size.convert(),
                                                macOut = resultPinned.addressOf(0)
                                        )
                                }
                        }
                }
                return result
        }
}