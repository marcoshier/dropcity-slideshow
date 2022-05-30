/*
package classes

import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.Drawer

class Director(drawer: Drawer): Animatable() {

    val loader = Loader(drawer)

    init {
        loader.loadMedia(3)
    }

    val chapters = loader.chapters

    var currentChapterIndex = 0
    val currentChapter = chapters[currentChapterIndex]

    fun prepare() {

        if(currentChapterIndex == 0) {
            val video = currentChapter.video
            video!!.play()

            val frames = mutableListOf<ColorBuffer>()

            video.newFrame.listen {

                frames.add(it.frame)
                currentChapter.updateFrames(frames)
            }

            video.ended.listen {
                video.dispose()
                currentChapterIndex++
                currentChapter.prepare()
            }
        }

        for(chapter in chapters) {
            chapter.prepare()
        }

    }


    fun draw() {
        chapters[currentChapterIndex].draw()
    }



}*/
