package com.github.takahirom.compose

import GlobalSnapshotManager
import android.content.Context
import android.widget.FrameLayout
import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class Node(val value: String = "", val children: MutableList<Node>)

@OptIn(InternalCoroutinesApi::class)
fun runApp(context: Context): FrameLayout {
    val composer = Recomposer(Dispatchers.Main)

    GlobalSnapshotManager.ensureStarted()
    val mainScope = MainScope()
    mainScope.launch(start = CoroutineStart.UNDISPATCHED) {
        withContext(coroutineContext + DefaultMonotonicFrameClock) {
            composer.runRecomposeAndApplyChanges()
        }
    }
    mainScope.launch {
        composer.state.collect {
            println("composer:$it")
        }
    }

    val rootDocument = FrameLayout(context)
    val node = Node("root", mutableListOf())
    Composition(TextApplier(node), composer).apply {
        setContent {
            Node()
        }
    }
    mainScope.launch {
        var nodeString = ""
        while (true) {
            val newNodeString = node.toString()
            if (nodeString != newNodeString) {
                nodeString = newNodeString
                println(nodeString)
            }
            delay(100)
        }
    }
    return rootDocument
}

@Composable
private fun Node() {
    ComposeNode<Node, TextApplier>(
        factory = {
            Node("hello world", mutableListOf())
        },
        update = {
//            set(text) {
//                this.value = text
//            }
        },
    )
}

class TextApplier(node: Node) : AbstractApplier<Node>(node) {
    override fun onClear() {
        current.children.clear()
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        current.children.add(index, instance)
    }

    override fun insertTopDown(index: Int, instance: Node) {
    }

    override fun move(from: Int, to: Int, count: Int) {
        // NOT Supported
        TODO()
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }
}
