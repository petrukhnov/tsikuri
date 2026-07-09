package com.petrukhnov.tsikuri

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Window
import javax.swing.JComponent
import javax.swing.JWindow
import javax.swing.SwingUtilities
import javax.swing.Timer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object DebugHelper {

    fun drawRedRectangle(
        topLeftX: Double,
        topLeftY: Double,
        width: Int,
        height: Int,
        durationMs: Int = 1000
    ) {
        drawRedRectangle(Rectangle(topLeftX.toInt(), topLeftY.toInt(), width, height), )
    }

    fun drawRedRectangle(
        rectangle: Rectangle,
        durationMs: Int = 1000
    ) {
        SwingUtilities.invokeLater {
            val window = JWindow().apply {
                type = Window.Type.POPUP
                isAlwaysOnTop = true
                background = Color(0, 0, 0, 255)
                bounds = rectangle
                contentPane.add(RectangleComponent())
                isVisible = true
            }

            Timer(durationMs) {
                window.dispose()
            }.apply {
                isRepeats = false
                start()
            }
        }
    }

    private class RectangleComponent : JComponent() {

        override fun paintComponent(graphics: Graphics) {
            super.paintComponent(graphics)

            val graphics2D = graphics as Graphics2D
            graphics2D.color = Color.RED
            graphics2D.stroke = BasicStroke(3f)
            graphics2D.drawRect(1, 1, width - 3, height - 3)
        }
    }
}
