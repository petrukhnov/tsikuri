package com.petrukhnov.tsikuri

import nu.pattern.OpenCV

object Main {
    init {
        OpenCV.loadLocally()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        ClickHelper.waitAndClickImage("button.png")

        val imgResult = ImageHelper.waitForImage("button.png")
        when (imgResult) {
            is ImageSearchResult.Found -> {
                MouseHelper.moveTo(imgResult.location)
            }
        }

    }
}
