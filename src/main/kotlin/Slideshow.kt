/*
import classes.Director
import classes.Loader
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.IntVector2
import org.openrndr.shape.Rectangle

val contentScale = 1

fun main() = application {
    configure {
        width = 4704 / contentScale
        height = 168 / contentScale
        position = IntVector2(0, -760)
        hideWindowDecorations = true
        windowAlwaysOnTop = true
    }
    program {
        val director = Director(drawer)

        director.prepare()

        keyboard.keyUp.listen {
            if(it.key == KEY_SPACEBAR) {
                if(director.currentChapter.video != null) {
                    director.currentChapter.video!!.dispose()
                    director.currentChapter.video = null
                }
                director.currentChapterIndex++
                director.chapters[director.currentChapterIndex++].prepare()
            }
        }

        extend(ScreenRecorder())

        extend {
           director.draw()

        }
    }
}*/
