package com.github.takahirom.compose

import GlobalSnapshotManager
import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import launchComposeInsideLogger


sealed class Node {
    val children = mutableListOf<Node>()

    class RootNode : Node() {
        override fun toString(): String {
            return buildString {
                appendLine("RootNode")
                children.forEachIndexed { index, node ->
                    if (index == children.lastIndex) {
                        appendLine("└── $node")
                    } else {
                        appendLine("├── $node")
                    }
                }
            }
        }
    }

    data class Node1(
        val value: String = "",
    ) : Node()

    data class Node2(
        val value: String = "",
    ) : Node()

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

    val rootNode = Node.RootNode()
    Composition(NodeApplier(rootNode), composer).apply {
        setContent {
            Content()
        }
        launchNodeLogger(mainScope, rootNode)
        launchComposeInsideLogger(composer, mainScope)
    }

}

private fun launchNodeLogger(
    mainScope: CoroutineScope,
    node: Node.RootNode
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
            Node.Node1("node1")
        },
        update = {
        },
    )
}

@Composable
private fun Node2() {
    ReusableComposeNode<Node, NodeApplier>(
        factory = {
            Node.Node2("node2")
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