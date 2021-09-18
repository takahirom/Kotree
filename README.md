# Kotree

A simple tool to display a text tree with Jetpack Compose.

```text
root
├── a
│   ├── c
│   │   └── j
│   │       ├── k
│   │       └── l
│   └── f
└── d
    ├── g
    └── h
```

```kotlin
fun main() {
    println(
        kotree {
            Content()
        }
    )
}
        
@Composable
private fun Content() {
    Node("root") {
        Node("a") {
            Node("c") {
                Node("j") {
                    Node("k")
                    Node("l")
                }
            }
            Node("f")
        }
        Node("d") {
            Node("g")
            Node("h")
        }
    }
}
```

