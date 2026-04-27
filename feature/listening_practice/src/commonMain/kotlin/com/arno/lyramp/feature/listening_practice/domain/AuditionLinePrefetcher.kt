package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.AuditionLine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class AuditionLinePrefetcher(
        private val picker: AuditionLinePickerUseCase,
) {
        private var channel: Channel<AuditionLine> = Channel(capacity = CHANNEL_CAPACITY)
        private var producerJob: Job? = null

        fun start(scope: CoroutineScope, language: String?) {
                close()
                channel = Channel(capacity = CHANNEL_CAPACITY)
                val ch = channel
                producerJob = scope.launch {
                        try {
                                picker.reset(language)
                                while (isActive) {
                                        val next = picker.nextLine() ?: break
                                        ch.send(next)
                                }
                                ch.close()
                        } catch (ce: CancellationException) {
                                ch.close(ce)
                                throw ce
                        } catch (e: Exception) {
                                ch.close(e)
                        }
                }
        }

        suspend fun next(): ChannelResult<AuditionLine> = channel.receiveCatching()

        fun close() {
                producerJob?.cancel()
                producerJob = null
                channel.close()
        }

        private companion object {
                const val CHANNEL_CAPACITY = 3
        }
}
