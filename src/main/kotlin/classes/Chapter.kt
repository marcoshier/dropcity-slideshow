/*
package classes

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.keyframer.Keyframer
import org.openrndr.extra.noise.Random
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.shape.IntRectangle
import org.openrndr.shape.Rectangle
import java.io.File

class Chapter(val files: MutableList<File>, val drawer: Drawer): Animatable(){

    val slots = mutableListOf<Pair<Int, ColorBuffer>>()
    val cbs = mutableListOf<ColorBuffer>()

    var video: VideoPlayerFFMPEG? = null
    var currentFrame = renderTarget(168 * 28, 168) {
        colorBuffer()
    }

    var activeImages = 0.0
    val imageDuration = 1000

    fun prepare() {

        var previousSlot = -1

        for(cb in cbs) {
            val cbSlots = cb.width / 168

            val slot = if(previousSlot <= 28) previousSlot + 1 else Random.int(0, 28 - cbSlots)
            previousSlot += cbSlots

            slots.add(Pair(slot, cb))
        }

        animate(::activeImages, cbs.size.toDouble(), imageDuration * cbs.size.toLong(), Easing.None).completed.listenOnce {
            println(activeImages)
        }
    }


    private fun loadCBs(full: Boolean = false) {
        //println("DIR ----- $chapterName, files: - ${files.map { it.name }}")
        files.forEachIndexed { index, file ->

            val width = if(full) 28 else Random.int(1, 3)

            val rect = Rectangle(0.0, 0.0, width * 168.0, 168.0)
            val rt = renderTarget(width * 168, 168) {
                colorBuffer()
            }

            val cb = loadImage(file)
            drawer.isolatedWithTarget(rt) {
                drawer.defaults()
                drawer.ortho()
                drawer.imageFit(cb, rect, fitMethod = FitMethod.Cover)
            }
            cbs.add(rt.colorBuffer(0))

            rt.destroy()

        }
    }


    fun draw() {
        Random.resetState()
        this.updateAnimation()


        if(video != null) {
            video!!.draw(drawer, blind = true)
            drawer.image(currentFrame.colorBuffer(0))
        } else {

            drawer.isolatedWithTarget(currentFrame) {
                slots.take(activeImages.toInt()).forEach { (xOffset, cb) ->
                    drawer.image(cb, xOffset * 168.0, 0.0, cb.width.toDouble(), 168.0)
                }
            }


            drawer.image(currentFrame.colorBuffer(0))

        }

    }

}
*/
