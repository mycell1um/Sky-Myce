package me.mycellium.skymyce.api.events

import me.mycellium.skymyce.api.DungeonChest
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

data class DungeonChestInitializedEvent(
    val floor: DungeonFloor,
    val chest: DungeonChest,
    val contents: Map<SkyBlockId, Int>
): SkyBlockEvent()