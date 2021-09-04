package com.github.takahirom.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.ReusableComposeNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

fun main() {
    runApp()
}
