package com.petrukhnov.tsikuri

import java.awt.Rectangle

private class ButtonSearchScript : ScriptTemplate("sbt") {

    override suspend fun playScript() {
        val result = ImageHelper.waitForImage("button.png")

        when(result) {
            is ImageSearchResult.Found -> {
                val rectangle = Rectangle(result.location.x.toInt()-50, result.location.y.toInt()+20, 100, 30)
                DebugHelper.drawRedRectangle(rectangle, 2000)
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