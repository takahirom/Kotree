# Kotree

Visualize text graph by Jetpack Compose

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

```
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

