package com.petrukhnov.tsikuri

import org.opencv.core.Point
import java.awt.MouseInfo
import java.awt.Robot

object MouseHelper {

    fun moveTo(destination: Point) {
        val durationMs: Long = 100 //todo make configurable or specific velocity
        val robot = Robot()
        val start = MouseInfo.getPointerInfo().location
        val startX = start.x
        val startY = start.y

        val steps = 100
        val delay = durationMs / steps
        for (i in 1..steps) {
            val t = i.toDouble() / steps
            val x = (startX + (destination.x - startX) * t).toInt()
            val y = (startY + (destination.y - startY) * t).toInt()

            robot.mouseMove(x, y)
            Thread.sleep(delay)
        }
    }
}