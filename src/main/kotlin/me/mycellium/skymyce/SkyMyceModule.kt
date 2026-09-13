package me.mycellium.skymyce

import java.lang.ref.WeakReference

abstract class SkyMyceModule {
    private var enabled = true

    init {
        SkyMyce.logger.info("Module Created: ${this::class.simpleName}")
        ModuleManager.instances += WeakReference(this)
    }

    fun enable() {
        if (!enabled) {
            enabled = true
            onEnable()
        }
    }

    fun disable() {
        if (enabled) {
            enabled = false
            onDisable()
        }
    }

    fun isEnabled(): Boolean = enabled

    open fun init() {}

    open fun tick() {}

    open fun onEnable() {}

    open fun onDisable() {}
}