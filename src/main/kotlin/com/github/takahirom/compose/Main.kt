package com.github.takahirom.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


sealed class Node {
    val children = mutableListOf<Node>()

    class RootNode : Node() {
        override fun toString(): String {
            return rootNodeToString()
        }
    }

    data class Node1(
        var name: String = "",
    ) : Node()

    data class Node2(
        var name: String = "",
    ) : Node()
}


@OptIn(InternalCoroutinesApi::class)
fun runApp() {
    val composer = Recomposer(Dispatchers.Main)

    GlobalSnapshotManager.ensureStarted()
    val mainScope = MainScope()
    mainScope.launch(DefaultChoreographerFrameClock) {
        composer.runRecomposeAndApplyChanges()
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
        delay(10000)
        state = false
    }
    if (state) {
        Node1()
    }
    Node2()
}

@Composable
private fun Node1(name: String = "node1") {
    ReusableComposeNode<Node.Node1, NodeApplier>(
        factory = {
            Node.Node1()
        },
        update = {
            set(name) { this.name = it }
        },
    )
}

@Composable
private fun Node2(name: String = "node2") {
    ReusableComposeNode<Node.Node2, NodeApplier>(
        factory = {
            Node.Node2()
        },
        update = {
            set(name) { this.name = it }
        },
    )
}


class NodeApplier(node: Node) : AbstractApplier<Node>(node) {
    override fun onClear() {
        current.children.clear()
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        // use top down
    }

    override fun insertTopDown(index: Int, instance: Node) {
        current.children.add(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }
}