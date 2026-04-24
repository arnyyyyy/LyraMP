package com.arno.lyramp.core.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private const val EDGE_DP = 24
private const val TRIGGER_DP = 80

fun Modifier.iosBackSwipe(enabled: Boolean, onBack: () -> Unit): Modifier {
        if (!enabled) return this
        return this.pointerInput(onBack) {
                val edgePx = EDGE_DP.dp.toPx()
                val triggerPx = TRIGGER_DP.dp.toPx()

                awaitEachGesture {
                        val first = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        if (first.position.x > edgePx) return@awaitEachGesture

                        var totalDx = 0f
                        var totalDy = 0f
                        var triggered = false

                        while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == first.id } ?: break
                                val delta = change.positionChange()
                                totalDx += delta.x
                                totalDy += delta.y

                                if (abs(totalDy) > abs(totalDx) && abs(totalDy) > 12f) break

                                if (totalDx > triggerPx) {
                                        triggered = true
                                        change.consume()
                                        break
                                }

                                if (!change.pressed) break
                                if (totalDx > 0f) change.consume()
                        }

                        if (triggered) onBack()
                }
        }
}
