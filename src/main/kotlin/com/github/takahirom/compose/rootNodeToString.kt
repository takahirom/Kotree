package com.github.takahirom.compose

fun Node.RootNode.rootNodeToString(): String {
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

