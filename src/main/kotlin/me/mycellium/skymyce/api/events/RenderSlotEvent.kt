package me.mycellium.skymyce.api.events

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

abstract class RenderSlotEvent : SkyBlockEvent() {
    abstract val slot: Slot
    abstract val graphics: GuiGraphicsExtractor

    class Before(override val graphics: GuiGraphicsExtractor, override val slot: Slot) : RenderSlotEvent()
    class After(override val graphics: GuiGraphicsExtractor, override val slot: Slot) : RenderSlotEvent()
}