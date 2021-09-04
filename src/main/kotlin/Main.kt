package com.github.takahirom.compose

import GlobalSnapshotManager
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import launchComposeInsideLogger


sealed class Node(open val value: String = "", open val children: MutableList<Node>) {
    data class Node1(override val value: String = "", override val children: MutableList<Node>) : Node(value, children)
    data class Node2(override val value: String = "", override val children: MutableList<Node>) : Node(value, children)
}

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

    val rootNode = Node.Node1("root", mutableListOf())
    Composition(NodeApplier(rootNode), composer).apply {
        setContent {
            Content()
        }
        launchNodeLogger(mainScope, rootNode)
//        launchComposeInsideLogger(mainScope)
    }

}

private fun launchNodeLogger(
    mainScope: CoroutineScope,
    node: Node.Node1
) {
    mainScope.launch {
        var nodeString = ""
        while (true) {
            val newNodeString = node.toString()
            if (nodeString != newNodeString) {
                nodeString = newNodeString
                println(nodeString)
            }
            yield()
        }
    }
}

@Composable
fun Content() {
    var state by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(12000)
        state = false
    }
    if (state) {
        Node1()
    }
    Node2()
}

@Composable
private fun Node1() {
    ReusableComposeNode<Node, NodeApplier>(
        factory = {
            Node.Node1("node1", mutableListOf())
        },
        update = {
        },
    )
}

@Composable
private fun Node2() {
    ReusableComposeNode<Node, NodeApplier>(
        factory = {
            Node.Node2("node2", mutableListOf())
        },
        update = {
        },
    )
}


class NodeApplier(node: Node) : AbstractApplier<Node>(node) {
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