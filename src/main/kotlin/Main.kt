package com.github.takahirom.compose

import GlobalSnapshotManager
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


data class Node(val value: String = "", val children: MutableList<Node>)

@OptIn(InternalCoroutinesApi::class)
fun runApp() {
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
}

@Composable
fun Content() {
    var state by remember { mutableStateOf(false) }
    LaunchedEffect(Unit){
        while(true){
            state = !state
            delay(3000)
        }
    }
    if(state) {
        Node()
    }
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
