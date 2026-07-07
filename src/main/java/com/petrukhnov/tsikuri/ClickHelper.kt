package com.petrukhnov.tsikuri

import org.opencv.core.Point
import java.awt.MouseInfo
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
                val previousLocation = MouseInfo.getPointerInfo().location

                try {
                    MouseHelper.moveTo(imageSearchResult.location)
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                } finally {
                    MouseHelper.moveTo(Point(previousLocation.x.toDouble(), previousLocation.y.toDouble()))
                }
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
