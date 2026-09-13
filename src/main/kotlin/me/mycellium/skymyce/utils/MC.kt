package me.mycellium.skymyce.utils

import me.mycellium.skymyce.SkyMyce
import net.minecraft.client.Minecraft

object MC {
    val instance: Minecraft = Minecraft.getInstance()
    val connection get() = instance.connection
    val fps get() = instance.fps
    val font get() = instance.font
    var screen get() = instance.screen
        set(value) = instance.setScreen(value)
    val player get() = instance.player!!
}