package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ModelDownloadRepository {
        private val modelDir: String
                get() = getAppFilesDir()

        private fun createDownloadClient(): HttpClient = HttpClient {
                followRedirects = true
                install(HttpRedirect) {
                        checkHttpMethod = false
                        allowHttpsDowngrade = false
                }
        }

        fun getModelFilePath(model: DownloadableModel) = "$modelDir/${model.fileName}"

        fun findDownloadedModel() = DownloadableModel.entries.firstOrNull { fileExists(getModelFilePath(it)) }

        fun deleteAllModels() {
                DownloadableModel.entries.forEach { model ->
                        deleteFile(getModelFilePath(model))
                        deleteFile("${getModelFilePath(model)}.tmp")
                }
        }

        fun deleteModel(model: DownloadableModel) {
                deleteFile(getModelFilePath(model))
                deleteFile("${getModelFilePath(model)}.tmp")
        }

        fun downloadModel(model: DownloadableModel): Flow<ModelDownloadState> = flow {
                emit(ModelDownloadState.Downloading(0f))

                val filePath = getModelFilePath(model)
                val tmpPath = "$filePath.tmp"
                val client = createDownloadClient()

                try {
                        deleteFile(tmpPath)

                        client.prepareGet(model.downloadUrl).execute { response ->
                                val totalBytes = response.contentLength() ?: -1
                                val channel = response.bodyAsChannel()
                                var downloadedBytes = 0
                                val buffer = ByteArray(64 * 1024)

                                val outputStream = openFileForWriting(tmpPath)
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

                                emit(ModelDownloadState.Downloaded)
                        }
                } catch (e: Exception) {
                        deleteFile(tmpPath)
                        emit(ModelDownloadState.Error(e.message ?: "Ошибка скачивания"))
                } finally {
                        client.close()
                }
        }.flowOn(Dispatchers.IO)
}
