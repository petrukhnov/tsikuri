package com.petrukhnov.tsikuri

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.awt.BasicStroke
import java.awt.Canvas
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Window

object DebugHelper {

    suspend fun drawRedRectangle(
        topLeftX: Double,
        topLeftY: Double,
        width: Int,
        height: Int,
        durationMs: Long = 1000
    ) {
        drawRedRectangle(Rectangle(topLeftX.toInt(), topLeftY.toInt(), width, height), )
    }

    suspend fun drawRedRectangle(rectangle: Rectangle, durationMs: Long = 1000) {

        lateinit var window: Window

        EventQueue.invokeAndWait {
            // Sometimes transparent windows not supported/not enabled on specific systems.
            // The workaround is to make a screenshot and then use it as a background.
            val screenshot = Robot().createScreenCapture(rectangle)

            window = Window(null).apply {
                isAlwaysOnTop = true
                bounds = rectangle
                background = Color.BLACK
            }

            val canvas = object : Canvas() {
                override fun paint(g: Graphics) {
                    val g2 = g.create() as Graphics2D
                    try {
                        //draw original content
                        g2.drawImage(screenshot, 0, 0, null)

                        //draw outline
                        g2.color = Color.RED
                        g2.stroke = BasicStroke(3f)
                        g2.drawRect(1, 1, width - 3, height - 3)
                    } finally {
                        g2.dispose()
                    }
                }
            }
            window.add(canvas)
            window.isVisible = true
        }

        delay(durationMs)

        EventQueue.invokeLater {
            window.dispose()
        }
    }

}
