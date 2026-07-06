package com.petrukhnov.tsikuri

import com.petrukhnov.tsikuri.ClickHelper.waitAndClickImage
import nu.pattern.OpenCV

object Main {
    init {
        OpenCV.loadLocally()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        waitAndClickImage("button.png")
    }
}
