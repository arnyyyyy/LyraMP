package com.arno.lyramp.feature.stories_generator.domain

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSOutputStream
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.getBytes

actual fun fileExists(path: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path)
}

@OptIn(ExperimentalForeignApi::class)
actual fun deleteFile(path: String) {
        NSFileManager.defaultManager.removeItemAtPath(path, null)
}

@OptIn(ExperimentalForeignApi::class)
actual fun renameFile(from: String, to: String) {
        NSFileManager.defaultManager.moveItemAtPath(from, toPath = to, error = null)
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun openFileForWriting(path: String): FileWriteStream {
        val url = NSURL.fileURLWithPath(path)
        val stream = NSOutputStream(uRL = url, append = false)
        stream.open()

        return object : FileWriteStream {
                override fun write(buffer: ByteArray, count: Int) {
                        buffer.usePinned { pinned ->
                                stream.write(
                                        pinned.addressOf(0).reinterpret(),
                                        count.convert()
                                )
                        }
                }

                override fun close() {
                        stream.close()
                }
        }
}

@OptIn(ExperimentalForeignApi::class)
actual fun readFileHead(path: String, count: Int): ByteArray {
        val data = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
        val length = minOf(count.toULong(), data.length).toInt()
        val result = ByteArray(length)
        result.usePinned { pinned ->
                data.getBytes(pinned.addressOf(0), length.toULong())
        }
        return result
}
