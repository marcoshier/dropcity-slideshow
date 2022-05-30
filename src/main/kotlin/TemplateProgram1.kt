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

        repeat(0.7) {
            slideshow.triggerNewImage()
        }
/*
        extend(ScreenRecorder())*/
        extend {
            drawer.stroke = null
            slideshow.draw()
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

    var slots = (0..28).map { Pair(it, false) }.toMutableList()

    val images = mutableListOf<Image>()
    var cbs = mutableListOf<ColorBuffer>()

    fun prepare(folderIndex: Int) {

        val dir = File(imagesPath).walk().drop(1).toMutableList()[folderIndex]
        val files = dir.listFiles().toMutableList()

        cbs = loadCBs(files)
    }


    var currentIndex = 0

    fun triggerNewImage() {

        val currentCB = cbs[currentIndex]
        var slotPosition = if(currentIndex % 2 == 0) Random.int(0, 28 - currentCB.width / 168) else 0
        //println(slots)

        var emptyFound = false

        for(j in slotPosition until slots.size - currentCB.width / 168) { // run through all the blocks
            val slot = slots[j]

            if(!slot.second) { // found an empty one!
                when(currentCB.width / 168) { // does it match the width? (are the consecutive ones also empty? based on width)
                    // if YES -> assign position!   if NO -> wait for the next repeat
                    1 -> { slotPosition = slot.first; emptyFound = true }
                    2 -> if(!slots[j + 1].second) { slotPosition = slot.first; emptyFound = true } else {println("No space available"); break }
                    3 -> if(!slots[j + 1].second && !slots[j + 2].second) { slotPosition = slot.first; emptyFound = true } else {println("No space available"); break }
                }
                break
            }
        }


        if(emptyFound) {
            println("Empty found! $slotPosition, ${currentCB.width / 168}")
            val image = Image(slotPosition, currentCB.width / 168, cbs[currentIndex])

            // assign active slots
            for(i in slotPosition until slotPosition + (currentCB.width / 168)) {
                slots[i] = Pair(i, true)
            }
            images.add(image)
            image.fadeIn()


            currentIndex++

            if(currentIndex == cbs.size) {
                currentIndex = 0
            }

        }


    }

    fun draw() {
        for(j in images.size - 1 downTo 0) {
            val image = images[j]
            if(image.isActive) {
                image.draw(drawer)
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




























