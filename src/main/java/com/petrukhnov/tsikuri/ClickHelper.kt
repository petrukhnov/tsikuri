package com.petrukhnov.tsikuri

import java.awt.Robot
import java.awt.event.InputEvent

object ClickHelper {

    fun clickImage(imagePath:  String) {
        val searchTemplate = ImageHelper.readImageResource(imagePath)
        val foundImage = ImageHelper.findImage(searchTemplate)
        clickImage(foundImage)
    }

    @JvmStatic
    fun waitAndClickImage(imagePath: String) {
        val searchTemplate = ImageHelper.readImageResource(imagePath)
        val foundImage = ImageHelper.waitForImage(searchTemplate)
        clickImage(foundImage)
    }

    private fun clickImage(imageSearchResult:  ImageSearchResult) {
        when(imageSearchResult) {
            is ImageSearchResult.Found -> {
                val robot = Robot()
                robot.mouseMove(imageSearchResult.location.x.toInt(), imageSearchResult.location.y.toInt())
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            }
            is ImageSearchResult.NotFound -> {
                //do nothing
            }
            is ImageSearchResult.Error -> {
                //do nothing
            }
        }
    }
}