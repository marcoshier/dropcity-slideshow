import org.openrndr.WindowMultisample
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.IntVector2
import org.openrndr.draw.*
import org.openrndr.extra.noise.Random
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import java.io.File
import org.openrndr.extra.timer.repeat
import org.openrndr.extra.videoprofiles.ProresProfile
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.svg.loadSVG


fun main() = application {
    configure {
        width = 4704
        height = 168
        position = IntVector2(0, -660)
        hideWindowDecorations = true
        windowAlwaysOnTop = true
        multisample = WindowMultisample.SampleCount(32)
    }

    program {

        var imageInterval = 0.0825
        var imageDuration = 4000L


        class Image(val slotPosition: Int, val slotsWidth: Int, val cb: ColorBuffer): Animatable() {

            val colors = listOf("#F0FA9F")

            var isActive = false


            private var colorFade = ColorRGBa.BLACK
            val thisColor = ColorRGBa.fromHex(colors.random())

            fun fadeIn() {


                this.cancel()
                colorFade = ColorRGBa.BLACK

                isActive = true

                animate(::colorFade, ColorRGBa.BLACK, 50, Easing.None).completed.listen { // fade in
                    animate(::colorFade, thisColor.opacify(1.0), 500, Easing.None).completed.listen { // fade in
                        animate(::colorFade, thisColor.opacify(1.0), 500, Easing.None).completed.listen { // fade in
                            animate(::colorFade, thisColor.opacify(1.0), imageDuration, Easing.None).completed.listen { // fade in
                                animate(::colorFade, thisColor.opacify(1.0), 500, Easing.None).completed.listen { // fade in
                                    animate(::colorFade, ColorRGBa.BLACK, 500, Easing.None).completed.listen { // fade in
                                        isActive = false
                                    }
                                }
                            }
                        }
                    }
                }
            }

            fun draw() {
                if(this.isActive) {

                    this.updateAnimation()

                    val rect = Rectangle(slotPosition * 168.0, 0.0, slotsWidth * 168.0, 168.0)

                    drawer.isolated {
                        drawer.defaults()
                        drawer.stroke = null
                        drawer.fill = colorFade
                        drawer.rectangle(rect)
                    }
                }
            }

        }


        var currentIndex = 0

        class Slideshow {

            var slots = (0..28).map { Pair(it, false) }.toMutableList()

            val images = mutableListOf<Image>()
            var cbs = mutableListOf<ColorBuffer>()

            fun prepare() {
                cbs = loadCBs((0..28).map { File("data/images/logo.svg") }.toMutableList())
            }


            fun triggerNewImage() {

                val currentCB = cbs[currentIndex]

                var slotPosition = currentIndex


                val image = Image(slotPosition, currentCB.width / 168, cbs[currentIndex])

                // assign active slots
                for(i in slotPosition until slotPosition + (currentCB.width / 168)) {
                    slots[i] = Pair(i, true)
                }
                images.add(image)
                image.fadeIn()


                currentIndex++

                if(currentIndex == cbs.size) {
                   println("finished")
                }




            }

            fun draw() {
                for(j in images.size - 1 downTo 0) {
                    val image = images[j]
                    if(image.isActive) {
                        image.draw()
                    } else {
                        if(slots[image.slotPosition].second) {
                            for(i in image.slotPosition until image.slotPosition + image.cb.width / 168) {
                                slots[i] = Pair(i, false)
                                images.remove(image)
                            }
                        }
                    }
                }

            }

            private fun loadCBs(files: MutableList<File>): MutableList<ColorBuffer> {

                val cbs = mutableListOf<ColorBuffer>()

                files.forEachIndexed { index, file ->
                    val svg = loadSVG(file)

                    val widthInSlots = 1


                    val resolved = colorBuffer(widthInSlots * 168, 168)
                    val rt = renderTarget(widthInSlots * 168, 168, multisample = BufferMultisample.SampleCount(32)) {
                        colorBuffer()
                        depthBuffer()
                    }


                    drawer.isolatedWithTarget(rt) {
                        drawer.defaults()
                        drawer.ortho()

                        drawer.composition(svg)

                        //drawer.imageFit(cb, rect, fitMethod = FitMethod.Cover)

                    }

                    rt.colorBuffer(0).copyTo(resolved)
                    cbs.add(resolved)

                    rt.destroy()

                }

                return cbs
            }

        }

        mouse.buttonUp.listen {
            currentIndex = 0
        }

        val slideshow = Slideshow()
        slideshow.prepare()

        repeat(imageInterval) {  // Midi?
            slideshow.triggerNewImage()
        }

        extend(ScreenRecorder()) {
            profile = ProresProfile()
            name = "INTROLONG"
            multisample = BufferMultisample.SampleCount(32)
        }

        val logo = loadImage("data/images/Logo_Long_Transparent-01.png")

        extend {
            drawer.stroke = null
            slideshow.draw()

                drawer.image(logo, 0.0, 0.0, width.toDouble(), height.toDouble())

        }
    }
}




























