package com.github.takahirom.compose

fun Node.nodeToString(depthArray: Array<Boolean> = arrayOf()): String {
    return buildString {
        children.forEachIndexed { index, node ->
            val indent = depthArray.drop(1).joinToString("") { if (it) "│   " else "    " }
            if (index == children.lastIndex) {
                val hasChildrenPrefix = if (depthArray.isEmpty()) {
                    ""
                } else {
                    "$indent└── "
                }
                if (node.children.isEmpty()) {
                    appendLine("$hasChildrenPrefix$node")
                } else {
                    appendLine("$hasChildrenPrefix$node")
                    append(node.nodeToString(depthArray + false))
                }
            } else {
                val noChildrenPrefix = if (depthArray.isEmpty()) {
                    ""
                } else {
                    "$indent├── "
                }
                if (node.children.isEmpty()) {
                    appendLine("$noChildrenPrefix$node")
                } else {
                    appendLine("$noChildrenPrefix$node")
                    append(node.nodeToString(depthArray + true))
                }
            }
        }
    }
}

