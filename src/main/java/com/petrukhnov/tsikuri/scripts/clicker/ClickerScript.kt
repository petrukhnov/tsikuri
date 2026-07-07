package com.petrukhnov.tsikuri.scripts.clicker

import com.petrukhnov.tsikuri.ScriptTemplate
import kotlinx.coroutines.delay
import org.jnativehook.GlobalScreen
import java.awt.Robot
import java.awt.event.InputEvent

/**
 *
 */
class ClickerScript: ScriptTemplate("tcl") {

    enum class Action {
        MORE, LESS
    }

    private var started = true
    private var frequency = 0
    private val robot = Robot()
    private lateinit var keyListener: ClickerKeyListener

    override suspend fun playScript() {
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

    override fun beforeStart() {
        //key listener
        GlobalScreen.registerNativeHook()
        keyListener = ClickerKeyListener(this)
        GlobalScreen.addNativeKeyListener(keyListener)
        println("ClickerScript started. Press PgUp/PgDn for clicks.")

    }

    override fun beforeStop() {
        println("ClickerScript stopping.")
        started = false
        GlobalScreen.removeNativeKeyListener(keyListener)
    }

    fun handle (action: Action) {
        when(action) {
            Action.MORE -> {
                if (frequency == 0) {
                    frequency = 1
                } else {
                    frequency *= 10
                }

                println("Clicker rate: $frequency")
            }
            Action.LESS -> {
                if (frequency < 1) {
                    frequency = 0
                } else {
                    frequency /= 10
                }
                println("Clicker rate: $frequency")
            }
        }
    }

}