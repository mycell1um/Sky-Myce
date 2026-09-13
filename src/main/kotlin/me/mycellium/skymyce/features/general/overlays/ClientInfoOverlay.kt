package me.mycellium.skymyce.features.general.overlays

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.misc.GeneralConfig
import me.mycellium.skymyce.hud.elements.StackElement
import me.mycellium.skymyce.hud.elements.TextElement
import me.mycellium.skymyce.hud.widget.Widget
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.ServerUtils
import kotlin.math.round

object ClientInfoOverlay : SkyMyceModule() {
    val widget = Widget("client_info", "Client Info", 10, 10, 1f) {
        if (!GeneralConfig.clientInfo) return@Widget null

        StackElement(
            children = listOf(
                TextElement("§9FPS: §f${MC.fps}"),
                TextElement("§9Ping: §f${ServerUtils.currentPing}ms §7(${ServerUtils.averagePing}ms avg)"),
                TextElement("§9TPS: §f${round(ServerUtils.tps * 100) / 100}"),
                TextElement("§9XYZ: §f${MC.player.blockX} ${MC.player.blockY} ${MC.player.blockZ}"),
            )
        )
    }
}