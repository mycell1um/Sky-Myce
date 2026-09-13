package me.mycellium.skymyce.api.events

import me.mycellium.skymyce.api.DungeonChest
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

data class DungeonChestRerollEvent(
    val floor: DungeonFloor,
    val chest: DungeonChest,
    val contents: Map<SkyBlockId, Int>,
    val slots: List<Slot>
): CancellableSkyBlockEvent()