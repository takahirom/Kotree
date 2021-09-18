package com.github.takahirom.compose

import kotlinx.coroutines.CoroutineScope

expect fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T