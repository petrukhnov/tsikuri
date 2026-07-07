package com.petrukhnov.tsikuri

import com.petrukhnov.tsikuri.scripts.clicker.ClickerScript
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.system.exitProcess

class ScriptRunner {

    private var job: Job? = null
    private var currentScript: ScriptTemplate? = null
    private var scripts: MutableMap<String, ScriptTemplate> = mutableMapOf()

    fun logConfig() {
        LogManager.getLogManager().reset()
        val loggerJnativehook = Logger.getLogger(GlobalScreen::class.java.`package`.name)
        loggerJnativehook.level = Level.OFF
        loggerJnativehook.useParentHandlers = false
    }

    fun keyConfig() {

        //key listener
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(object :NativeKeyListener{
            override fun nativeKeyTyped(event: NativeKeyEvent) {
            }

            override fun nativeKeyReleased(event: NativeKeyEvent) {
            }

            override fun nativeKeyPressed(event: NativeKeyEvent) {

                //insert
                if(event.keyCode == 3666) {

                }

                //delete
                if(event.keyCode == 3667) {
                    //stop scripts
                    currentScript?.active = false
                }

                //home
                if(event.keyCode == 3655) {
                    //todo something?
                    exitProcess(0)
                }

                //end
                if(event.keyCode == 3663) {
                    //exit
                    exitProcess(0)
                }
            }
        })

    }

    fun listenInput() {
        println("type commands: x ${scripts.keys.joinToString(" ")}")
        while (true) {
            print("> ")
            val input = readln()

            when (input) {
                //exit script
                "x" -> {
                    currentScript?.let { script ->
                        println("stopping script: ${script.scriptCode}")
                        script.stop()
                        currentScript = null
                        job?.cancel()
                    } ?: println("No active script running")
                }
                // run existing script
                else -> {
                    val selectedScript = scripts[input] ?: run {
                        println("Script template '$input' not found.")
                        continue
                    }
                    currentScript = selectedScript
                    selectedScript.active = true
                    job = CoroutineScope(Dispatchers.Default).launch  {
                        selectedScript.playScriptLoop()
                    }
                }
            }
        }
    }

    fun loadScripts() {
        addScript(ClickerScript())
    }

    fun addScript(scriptTemplate: ScriptTemplate) {
        scripts[scriptTemplate.scriptCode] = scriptTemplate
    }

}

fun main() {
    val scriptRunner = ScriptRunner()

    scriptRunner.logConfig()
    scriptRunner.keyConfig()
    scriptRunner.loadScripts()

    scriptRunner.listenInput()

}