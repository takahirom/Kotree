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

sealed class Node {
    val children = mutableListOf<Node>()

    class RootNode : Node() {
        override fun toString(): String {
            return nodeToString()
        }
    }

    data class TreeNode(
        var name: String = "",
    ) : Node() {
        override fun toString(): String {
            return name
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
fun kotree(content: @Composable () -> Unit) = runBlocking {
    val composer = Recomposer(Dispatchers.Main)

    GlobalSnapshotManager.ensureStarted()
    val mainScope = MainScope()
    mainScope.launch(YieldFrameClock) {
        composer.runRecomposeAndApplyChanges()
    }

    val rootNode = Node.RootNode()
    Composition(NodeApplier(rootNode), composer).apply {
        setContent(content)
//        launchNodeLogger(mainScope, rootNode)
//        launchComposeInsideLogger(composer, mainScope)
    }
    composer.awaitIdle()
    rootNode.nodeToString()
}

private fun launchNodeLogger(
    mainScope: CoroutineScope,
    node: Node.RootNode
) {
    mainScope.launch(Dispatchers.Default) {
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
fun Node(name: String = "no name", content: @Composable () -> Unit = {}) {
    ReusableComposeNode<Node.TreeNode, NodeApplier>(
        factory = {
            Node.TreeNode()
        },
        update = {
            set(name) { this.name = name }
        },
        content = content
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