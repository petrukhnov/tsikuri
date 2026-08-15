package com.petrukhnov.tsikuri

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Window

/**
 * Work in progress. Don't use it yet.
 */
object OverlayDebugHelper: Window(null) {


    private var debugShape: Shape? = null

    init {

        val device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .defaultScreenDevice

        if (!device.isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT
            )
        ) {
            error("Transparent windows are not supported")
        }


        background = Color(0, 0, 0, 0)
        isAlwaysOnTop = true
        type = Type.UTILITY
        val multimonitorBounds = device.defaultConfiguration.bounds

        bounds = multimonitorBounds
    }

    fun drawRedRectangle(
        topLeftX: Double,
        topLeftY: Double,
        width: Int,
        height: Int,
        durationMs: Long = 1000
    ) {
        drawRedRectangle(Rectangle(topLeftX.toInt(), topLeftY.toInt(), width, height), durationMs)
    }

    fun drawRedRectangle(
        rectangle: Rectangle,
        durationMs: Long = 1000
    ) {

        debugShape = rectangle
        isVisible = true
        repaint()

        Thread.sleep(durationMs)

        debugShape = null
        isVisible = false
        repaint()
    }

    override fun paint(g: Graphics) {
        debugShape ?: return

        val g2 = g.create() as Graphics2D
        try {
            g2.color = Color.RED
            g2.stroke = BasicStroke(3f)
            g2.draw(debugShape)
        } finally {
            g2.dispose()
        }
    }

    private fun readResolve(): Any = OverlayDebugHelper
}
