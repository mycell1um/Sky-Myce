package me.mycellium.skymyce.features.dev

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.DungeonApi.skyblockCount
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemDebugTooltipEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId

object DevTooltips : SkyMyceModule() {
    @Subscription
    fun onTooltipRender(event: ItemDebugTooltipEvent) {
        event.add(Component.literal("§8${event.item.getSkyBlockId()?.id} (x${event.item.skyblockCount()})"))
    }
}