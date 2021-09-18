package com.github.takahirom.compose

import kotlinx.coroutines.CoroutineScope

actual fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T = kotlinx.coroutines.runBlocking<T>(block = block)