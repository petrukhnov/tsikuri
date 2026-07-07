package com.petrukhnov.tsikuri.scripts.clicker

import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

class ClickerKeyListener(private val clickerScript: ClickerScript): NativeKeyListener{

    override fun nativeKeyTyped(event: NativeKeyEvent) {
    }

    override fun nativeKeyReleased(event: NativeKeyEvent) {
    }

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        //pg up
        if (event.keyCode == 3657) {
            //faster clicker
            clickerScript.handle(ClickerScript.Action.MORE)
        }

        //pg down
        if (event.keyCode == 3665) {
            //slower clicker
            clickerScript.handle(ClickerScript.Action.LESS)
        }

    }
}