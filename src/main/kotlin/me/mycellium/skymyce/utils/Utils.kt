package me.mycellium.skymyce.utils

import me.mycellium.skymyce.config.Config
import net.minecraft.network.chat.Component


object Utils {
    fun displayModMessage(string: String) {
        displayModMessage(Component.literal(string))
    }

    fun displayModMessage(component: Component) {
        MC.player.sendSystemMessage(Component.literal("§dSkyMyce §r§5» ").append(component))
    }

    fun displayMessage(string: String) {
        displayMessage(Component.literal(string))
    }

    fun displayMessage(component: Component) {
        MC.player.sendSystemMessage(component)
    }

    fun displayDevMessage(string: String) {
        if (Config.devMode) MC.player.sendSystemMessage(Component.literal("§dSkyMyce §bDev §r§5» ").append(Component.literal(string)))
    }

    fun displayTitle(title: String, subtitle: String) {
        displayTitle(Component.literal(title), Component.literal(subtitle))
    }

    fun displayTitle(title: Component, subtitle: Component) {
        MC.instance.gui.setTitle(title)
        MC.instance.gui.setSubtitle(subtitle)
        MC.instance.gui.setTimes(10, 40, 10)
    }
}
