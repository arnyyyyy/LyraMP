package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import com.arno.lyramp.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.request.headers
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class ModelDownloadRepository {
        private val modelDir: String
                get() = getAppFilesDir()

        private val client: HttpClient = HttpClient {
                followRedirects = true
                install(HttpRedirect) {
                        checkHttpMethod = false
                        allowHttpsDowngrade = false
                }
        }

        fun getModelFilePath(model: DownloadableModel) = "$modelDir/${model.fileName}"
        private fun getTmpPath(model: DownloadableModel) = "${getModelFilePath(model)}.tmp"
        private fun getMetaPath(model: DownloadableModel) = "${getModelFilePath(model)}.meta"

        fun findDownloadedModel() = DownloadableModel.entries.firstOrNull { fileExists(getModelFilePath(it)) }

        fun getPartialBytes(model: DownloadableModel): Long {
                val tmp = getTmpPath(model)
                return if (fileExists(tmp)) fileSize(tmp) else 0L
        }

        fun getSavedTotalBytes(model: DownloadableModel): Long {
                val metaPath = getMetaPath(model)
                if (!fileExists(metaPath)) return -1L
                return try {
                        readFileHead(metaPath, 32).decodeToString().trim().toLong()
                } catch (_: Exception) {
                        -1L
                }
        }

        fun getSavedProgress(model: DownloadableModel): Float {
                val partialBytes = getPartialBytes(model)
                val totalBytes = getSavedTotalBytes(model)
                return if (partialBytes > 0L && totalBytes > 0L) {
                        (partialBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                } else {
                        0f
                }
        }

        private fun saveTotalBytes(model: DownloadableModel, totalBytes: Long) {
                if (totalBytes <= 0L) return
                try {
                        val bytes = totalBytes.toString().toByteArray()
                        val out = openFileForWriting(getMetaPath(model), append = false)
                        out.write(bytes, bytes.size)
                        out.close()
                } catch (e: Exception) {
                        Log.logger.e("AAAMODELDOWNLOAD", e)
                }
        }

        fun deleteAllModels() {
                DownloadableModel.entries.forEach { model ->
                        deleteFile(getModelFilePath(model))
                        deleteFile(getTmpPath(model))
                        deleteFile(getMetaPath(model))
                }
        }

        fun deleteModel(model: DownloadableModel) {
                deleteFile(getModelFilePath(model))
                deleteFile(getTmpPath(model))
                deleteFile(getMetaPath(model))
        }

        fun downloadModel(
                model: DownloadableModel,
                resumeFromBytes: Long = 0L,
        ): Flow<ModelDownloadState> = flow {
                val filePath = getModelFilePath(model)
                val tmpPath = getTmpPath(model)

                try {
                        val requestedResume = resumeFromBytes > 0 && fileExists(tmpPath)
                        val startBytes = if (requestedResume) fileSize(tmpPath) else 0L

                        if (!requestedResume) {
                                deleteFile(tmpPath)
                                deleteFile(getMetaPath(model))
                        }

                        emit(ModelDownloadState.Downloading(if (requestedResume) getSavedProgress(model) else 0f))

                        client.prepareGet(model.downloadUrl) {
                                if (startBytes > 0L) {
                                        headers { append("Range", "bytes=$startBytes-") }
                                }
                        }.execute { response ->
                                val statusCode = response.status.value
                                val resuming = statusCode == 206 && startBytes > 0L

                                if (!resuming && startBytes > 0L) {
                                        deleteFile(tmpPath)
                                        deleteFile(getMetaPath(model))
                                }

                                val rangeTotal = response.headers["Content-Range"]
                                        ?.substringAfterLast('/', "")
                                        ?.toLongOrNull()
                                val contentLength = response.contentLength() ?: -1L
                                val totalBytes = rangeTotal
                                        ?: if (resuming && contentLength > 0) startBytes + contentLength
                                        else contentLength

                                if (totalBytes > 0L) saveTotalBytes(model, totalBytes)

                                val channel = response.bodyAsChannel()
                                var downloadedBytes = if (resuming) startBytes else 0L
                                val buffer = ByteArray(64 * 1024)

                                val outputStream = openFileForWriting(tmpPath, append = resuming)
                                try {
                                        while (!channel.isClosedForRead) {
                                                val bytesRead = channel.readAvailable(buffer)
                                                if (bytesRead <= 0) break

                                                outputStream.write(buffer, bytesRead)
                                                downloadedBytes += bytesRead

                                                if (totalBytes > 0) {
                                                        val progress = (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                                                        emit(ModelDownloadState.Downloading(progress))
                                                }
                                        }
                                } finally {
                                        outputStream.close()
                                }

                                if (downloadedBytes < 1000) {
                                        deleteFile(tmpPath)
                                        emit(ModelDownloadState.Error("Не удалось скачать. Попробуйте ещё раз."))
                                        return@execute
                                }

                                val head = readFileHead(tmpPath, 4)
                                val magic = head.decodeToString()
                                if (magic != "GGUF") {
                                        deleteFile(tmpPath)
                                        emit(ModelDownloadState.Error("Файл повреждён. Попробуйте ещё раз."))
                                        return@execute
                                }

                                renameFile(tmpPath, filePath)
                                deleteFile(getMetaPath(model))

                                emit(ModelDownloadState.Downloaded)
                        }
                } catch (ce: CancellationException) {
                        throw ce
                } catch (e: Exception) {
                        val partialBytes = getPartialBytes(model)
                        if (partialBytes > 0L) {
                                emit(ModelDownloadState.Paused(getSavedProgress(model)))
                        } else {
                                emit(ModelDownloadState.Error(e.message ?: "Ошибка скачивания"))
                        }
                }
        }.flowOn(Dispatchers.IO)
}
