package com.petrukhnov.tsikuri

import org.opencv.core.Point
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent

object ClickHelper {

    private val robot = Robot()
    var returnMouse = false

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
        clickImage(imageSearchResultFound, 0, 0)
    }

    fun clickImage(imageSearchResultFound:  ImageSearchResult.Found, offsetX: Int = 0, offsetY: Int = 0) {
        moveAndClick(imageSearchResultFound.location.x, imageSearchResultFound.location.y, offsetX, offsetY)
    }

    fun moveAndClick(x: Double, y: Double, offsetX: Int = 0, offsetY: Int = 0) {
        val previousLocation = MouseInfo.getPointerInfo().location

        try {
            MouseHelper.moveTo(Point(x+offsetX, y+offsetY))
            moveAndClick()
        } finally {
            if (returnMouse) {
                MouseHelper.moveTo(Point(previousLocation.x.toDouble(), previousLocation.y.toDouble()))
            }
        }
    }

    fun moveAndClick() {
        Thread.sleep(100)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(100)
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
