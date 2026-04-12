package com.arno.lyramp.feature.stories_generator.domain

expect fun getAppFilesDir(): String
expect fun fileExists(path: String): Boolean
expect fun deleteFile(path: String)
expect fun renameFile(from: String, to: String)


interface FileWriteStream {
        fun write(buffer: ByteArray, count: Int)
        fun close()
}

expect fun openFileForWriting(path: String): FileWriteStream
expect fun readFileHead(path: String, count: Int): ByteArray
