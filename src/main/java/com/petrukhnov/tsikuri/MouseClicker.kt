package com.petrukhnov.tsikuri

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Robot
import java.awt.event.InputEvent

/**
 * Simple mouse clicker. Starts with 10 clicks/second.
 */
object MouseClicker {

    private var started = false
    private var frequency = 10
    private val robot = Robot()

    fun moreClicks() {

        if (!started) {
            started = true
            GlobalScope.launch {
                loop()
            }
        } else {
            //increase clicks
            frequency *= 10
        }
    }

    fun lessClicks() {

        if(frequency <= 10) {
            //stop
            started = false
        } else {
            //reduce clicks
            frequency /= 10
        }
    }

    private suspend fun loop() {

        while(started) {

            val startTime = System.currentTimeMillis()

            //clicks
            for (i in 0..frequency) {

                //stop, if needed
                if (!started) {
                    return
                }

                //fast clicks
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
            }

            val endTime = System.currentTimeMillis()
            //wait 1 second max
            delay(1000 - (endTime - startTime))
        }
    }
}