package me.mycellium.skymyce

import com.google.common.reflect.Reflection
import java.lang.ref.WeakReference

object ModuleManager {
    val instances = mutableListOf<WeakReference<SkyMyceModule>>()

    fun loadModules(vararg modules: SkyMyceModule) {
    }

    val modules: List<SkyMyceModule>
        get() {
            instances.removeIf { it.get() == null }
            return instances.mapNotNull { it.get() }
        }

    val enabledModules: List<SkyMyceModule>
        get() = modules.filter { it.isEnabled() }
}