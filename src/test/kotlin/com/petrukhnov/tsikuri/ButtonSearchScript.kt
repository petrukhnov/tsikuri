package com.petrukhnov.tsikuri

import java.awt.Rectangle

private class ButtonSearchScript : ScriptTemplate("bss") {

    override suspend fun playScript() {
        val imgSize = ImageHelper.readImageResource("button.png").size()
        val result = ImageHelper.waitForImage("button.png")

        when(result) {
            is ImageSearchResult.Found -> {
                val imgRectangle = Rectangle(result.location.x.toInt()-imgSize.width.toInt()/2, result.location.y.toInt()-imgSize.height.toInt()/2, imgSize.width.toInt(), imgSize.height.toInt())
                DebugHelper.drawRedRectangle(imgRectangle, 2000L)
                Thread.sleep(3_000)

                val rectangle = Rectangle(result.location.x.toInt()-50, result.location.y.toInt()+20, 100, 30)
                DebugHelper.drawRedRectangle(rectangle, 2000L)
                Thread.sleep(3_000)
                val text = TextRecognitionHelper.readText(rectangle)
                println("found: $text")
            }
        }


        println("button.png search result: $result")
        active = false
    }
}

fun main() {
    val scriptRunner = ScriptRunner()

    scriptRunner.logConfig()
    scriptRunner.keyConfig()
    scriptRunner.loadScripts()
    scriptRunner.addScript(ButtonSearchScript())

    scriptRunner.listenInput()

}