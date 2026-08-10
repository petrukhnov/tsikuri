package com.petrukhnov.tsikuri

import org.opencv.core.Point
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent

object ClickHelper {

    private val robot = Robot()

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

    fun clickImage(imageSearchResultFound:  ImageSearchResult.Found) {
        val previousLocation = MouseInfo.getPointerInfo().location

        try {
            MouseHelper.moveTo(imageSearchResultFound.location)
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        } finally {
            MouseHelper.moveTo(Point(previousLocation.x.toDouble(), previousLocation.y.toDouble()))
        }
    }

    private fun clickImage(imageSearchResult:  ImageSearchResult) {
        when(imageSearchResult) {
            is ImageSearchResult.Found -> {
                clickImage(imageSearchResult)
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
