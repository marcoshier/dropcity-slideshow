/*
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.math.IntVector2
import kotlin.math.cos
import kotlin.math.sin
import org.openrndr.draw.*
import org.openrndr.extra.noise.Random
import org.openrndr.extras.imageFit.FitMethod
import org.openrndr.extras.imageFit.imageFit
import org.openrndr.shape.Rectangle
import java.io.File
import org.openrndr.extra.timer.repeat
import org.openrndr.ffmpeg.ScreenRecorder


fun main() = application {
    configure {
        width = 4704
        height = 168
        position = IntVector2(0, -660)
        hideWindowDecorations = true
        windowAlwaysOnTop = true
    }

    program {

        val slideshow = Slideshow(drawer)
        slideshow.prepare(0)

        repeat(5.0) {
            slideshow.triggerNewImage()
        }

        extend(ScreenRecorder())
        extend {
            drawer.stroke = null
            slideshow.draw()

            println(height.toDouble())
            val rect = Rectangle(0.0, 0.0, 4 * 168.0, height.toDouble())

            drawer.stroke = ColorRGBa.BLUE
            drawer.fill = null
            drawer.rectangle(rect)

        }
    }
}

class Image(val slotPosition: Int, val slotsWidth: Int, val cb: ColorBuffer): Animatable() {

    var isActive = false

    private var colorFade = ColorRGBa.BLACK
    var imageDuration = 7000L

    fun fadeIn() {
        this.cancel()


        colorFade = ColorRGBa.BLACK

        isActive = true
        animate(::colorFade, ColorRGBa.BLACK, 50, Easing.None).completed.listen { // fade in
            animate(::colorFade, ColorRGBa.fromHex("#EDFB92").opacify(1.0), 2000, Easing.None).completed.listen { // fade in
                animate(::colorFade, ColorRGBa.fromHex("#EDFB92").opacify(0.0), 2000, Easing.None).completed.listen { // fade in
                    animate(::colorFade, ColorRGBa.fromHex("#EDFB92").opacify(0.0), imageDuration, Easing.None).completed.listen { // fade in
                        animate(::colorFade, ColorRGBa.fromHex("#EDFB92").opacify(1.0), 2000, Easing.None).completed.listen { // fade in
                            animate(::colorFade, ColorRGBa.BLACK, 2000, Easing.None).completed.listen { // fade in
                                isActive = false
                            }
                        }
                    }
                }
            }
        }
    }

    fun draw(drawer: Drawer) {
        if(this.isActive) {

            this.updateAnimation()

            val rect = Rectangle(slotPosition * 168.0, 0.0, slotsWidth * 168.0, 168.0)


            drawer.image(cb, rect.x, rect.y, rect.width, rect.height)

            drawer.isolated {
                drawer.defaults()
                drawer.fill = colorFade
                drawer.rectangle(rect)
            }
        }
    }

}


class Slideshow(val drawer: Drawer){
    val imagesPath = "data/images/Chapters/"

    val slots = (0..28).map { Pair(it, false) }.toMutableList()

    val images = mutableListOf<Image>()
    var cbs = mutableListOf<ColorBuffer>()

    fun prepare(folderIndex: Int) {

        val dir = File(imagesPath).walk().drop(1).toMutableList()[folderIndex]
        val files = dir.listFiles().toMutableList()

        cbs = loadCBs(files)
    }


    var currentIndex = 0

    fun triggerNewImage() {
        if(images.size != cbs.size) {

            val unoccupied = slots.filter { !it.second }.map { it.first }

            val slotsWidth = cbs[currentIndex].width / 168
            val slotPosition = Random.int(max = unoccupied.size)

            for(j in slotPosition until slotsWidth) {
                slots[j] = Pair(j, true)
            }

            val image = Image(slotPosition, slotsWidth, cbs[currentIndex])
            images.add(image)

            images[currentIndex].fadeIn()

            currentIndex++

        } else {
            println("Finished images")
        }

    }

    fun draw() {
        for(image in images.filter { it.isActive }) {
            image.draw(drawer)
        }
    }

    private fun loadCBs(files: MutableList<File>): MutableList<ColorBuffer> {

        val cbs = mutableListOf<ColorBuffer>()

        files.forEachIndexed { index, file ->
            val cb = loadImage(file)

            val widthInSlots = Random.int(1, 3)
            val rect = Rectangle(0.0, 0.0, widthInSlots * 168.0, 168.0)

            val rt = renderTarget(widthInSlots * 168, 168) {
                colorBuffer()
            }

            drawer.isolatedWithTarget(rt) {
                drawer.defaults()
                drawer.ortho()
                drawer.imageFit(cb, rect, fitMethod = FitMethod.Cover)

            }
            cbs.add(rt.colorBuffer(0))

            rt.destroy()

        }

        return cbs
    }

}




























*/
