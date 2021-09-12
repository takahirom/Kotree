package com.github.takahirom.compose

import android.view.Choreographer
import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

object DefaultChoreographerFrameClock : MonotonicFrameClock {
    private val choreographer = runBlocking(Dispatchers.Main.immediate) {
        Choreographer.getInstance()
    }

    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R
    ): R = suspendCancellableCoroutine<R> { co ->
        val callback = Choreographer.FrameCallback { frameTimeNanos ->
            co.resumeWith(runCatching { onFrame(frameTimeNanos) })
        }
        choreographer.postFrameCallback(callback)
        co.invokeOnCancellation { choreographer.removeFrameCallback(callback) }
    }
}
