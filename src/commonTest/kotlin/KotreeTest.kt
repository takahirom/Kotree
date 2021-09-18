import androidx.compose.runtime.Composable
import com.github.takahirom.compose.Node
import com.github.takahirom.compose.kotree
import kotlin.test.Test
import kotlin.test.assertEquals

class KotreeTest {
    @Test
    fun print() {
        println(
            kotree {
                Content()
            }
        )
    }

    @Test
    fun test() {
        assertEquals(
            expected = """
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
        
        """.trimIndent(),
            actual = kotree {
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
}