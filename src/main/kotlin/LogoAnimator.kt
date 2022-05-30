import org.openrndr.application
import org.openrndr.math.IntVector2

fun main() = application {
    configure {
        width = 4704
        height = 168
        position = IntVector2(0, -660)
        hideWindowDecorations = true
        windowAlwaysOnTop = true
    }
    program {
        extend {

        }
    }
}