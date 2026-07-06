package com.petrukhnov.tsikuri

import nu.pattern.OpenCV
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.concurrent.CountDownLatch
import kotlin.system.exitProcess

/**
 * This class is for development testing only and will be removed in the future.
 */
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

        //add key config
        keyConfig()
        //infinite loop to test keys
        val latch = CountDownLatch(1)
        Runtime.getRuntime().addShutdownHook(Thread {
            latch.countDown()
        })
        println("Running...")
        latch.await()


    }

    /**
     *
     */
    fun keyConfig() {
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(object :NativeKeyListener{

            override fun nativeKeyTyped(event: NativeKeyEvent) {
            }

            override fun nativeKeyReleased(event: NativeKeyEvent) {
            }

            override fun nativeKeyPressed(event: NativeKeyEvent) {

                //end
                if(event.keyCode == 3663) {
                    //exit
                    exitProcess(0)
                }

                //pg up
                if(event.keyCode == 3657) {
                    //faster clicker
                    MouseClicker.moreClicks()
                }

                //pg down
                if(event.keyCode == 3665) {
                    //slower clicker
                    MouseClicker.lessClicks()
                }


            }
        })

    }
}
