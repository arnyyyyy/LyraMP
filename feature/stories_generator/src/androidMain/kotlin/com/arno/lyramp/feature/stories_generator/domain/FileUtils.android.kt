package com.arno.lyramp.feature.stories_generator.domain

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

actual fun fileExists(path: String): Boolean = File(path).exists()

actual fun deleteFile(path: String) {
        File(path).delete()
}

actual fun renameFile(from: String, to: String) {
        File(from).renameTo(File(to))
}

actual fun fileSize(path: String): Long {
        val f = File(path)
        return if (f.exists()) f.length() else 0L
}

actual fun openFileForWriting(path: String, append: Boolean): FileWriteStream {
        val fos = FileOutputStream(path, append)
        return object : FileWriteStream {
                override fun write(buffer: ByteArray, count: Int) {
                        fos.write(buffer, 0, count)
                }

                override fun close() {
                        fos.close()
                }
        }
}

actual fun readFileHead(path: String, count: Int): ByteArray {
        return FileInputStream(path).use { fis ->
                val buf = ByteArray(count)
                val read = fis.read(buf)
                if (read < count) buf.copyOf(read) else buf
        }
}
