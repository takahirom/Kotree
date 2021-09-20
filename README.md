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


Not only can you draw the tree manually, but you can also visualize the structure.

```kotlin
sampleproject
├── src
│   └── commonMain
│       └── kotlin
├── build.gradle.kts
└── settings.gradle.kts
```


```kotlin
@Composable
private fun FileNode(file: File) {
    Node(file.name) {
        file.listFiles().forEach {
            FileNode(it)
        }
    }
}
```
