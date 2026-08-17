package com.petrukhnov.tsikuri

import org.opencv.core.Point
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent

object ClickHelper {

    private val robot = Robot()
    var returnMouse = false

    fun clickImage(imagePath:  String) {
        clickImage(imagePath, 0, 0)
    }

    fun clickImage(imagePath:  String, offsetX: Int, offsetY: Int) {
        val searchTemplate = ImageHelper.readImageResource(imagePath)
        val foundImage = ImageHelper.findImage(searchTemplate)
        clickImage(foundImage, offsetX, offsetY)
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
            click()
        } finally {
            if (returnMouse) {
                MouseHelper.moveTo(Point(previousLocation.x.toDouble(), previousLocation.y.toDouble()))
            }
        }
    }

    fun click() {
        Thread.sleep(100)
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(100)
    }

    private fun clickImage(imageSearchResult:  ImageSearchResult, offsetX: Int = 0, offsetY: Int = 0) {
        when(imageSearchResult) {
            is ImageSearchResult.Found -> {
                clickImage(imageSearchResult, offsetX, offsetY)
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
