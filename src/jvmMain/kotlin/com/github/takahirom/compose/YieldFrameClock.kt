package com.github.takahirom.compose

import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.yield

actual object YieldFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R
    ): R {
        yield()
        return onFrame(System.nanoTime())
    }
}