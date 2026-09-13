package me.mycellium.skymyce.features.general

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.PlayerMessageEvent
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription

object Waypoints : SkyMyceModule() {
    var locationMap: MutableMap<BlockPos, Pair<String, Long>> = mutableMapOf()
    val waypointRegex = Regex("(?i)x: (-?[\\d,.]+),? y: (-?[\\d,.]+),? z: (-?[\\d,.]+)( (.*?))?")

    @Subscription
    fun onPartyChatEvent(event: PlayerMessageEvent) {
//        if (!(event.type == PlayerMessageType.PARTY || event.type == PlayerMessageType.PUBLIC)) return
//        waypointRegex.find(event.message)?.destructured?.let { (x, y, z, message) ->
//            locationMap[BlockPos(x.toInt(), y.toInt(), z.toInt())] = message to System.currentTimeMillis()
//        }
    }
}