package com.petrukhnov.tsikuri


abstract class ScriptTemplate(val scriptCode: String) {

    var active = false

    /**
     * Main loop in the separate thread.
     */
    internal suspend fun playScriptLoop() {

        beforeStart()
        while (active) {
            playScript()
        }
    }

    /**
     * Internal to this lib, indicate that should be stopped, then call the script-specific stopScript() method.
     */
    internal open fun stop() {
        beforeStop()
        active = false

    }

    protected open fun beforeStart() {

    }

    protected open fun beforeStop() {

    }

    abstract suspend fun playScript()


}
