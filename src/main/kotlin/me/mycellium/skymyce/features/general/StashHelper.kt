package me.mycellium.skymyce.features.general

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.RenderSlotEvent
import me.mycellium.skymyce.config.misc.StashHelperConfig
import me.mycellium.skymyce.utils.MC
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.SlotClickEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object StashHelper : SkyMyceModule() {
    private val materialRegex = Regex("^(.+) x(\\d+)$")
    private var worthlessItems: MutableSet<Slot>? = null

    @Subscription
    fun onOpenContainer(event: ContainerInitializedEvent) {
        if (!StashHelperConfig.stashHelper) return

        if (event.title != "View Stash") {
            worthlessItems = null
            return
        }

        worthlessItems = mutableSetOf()
        for (slot in event.containerSlots) {
            if (slot.container is Inventory) return
            val id = slot.item.getSkyBlockId() ?: return
            val count = materialRegex.find(slot.item.hoverName.stripped)?.groupValues[2]?.toInt() ?: 1
            val value = Pricing.getPrice(id.bazaarId)
            if ((value * count) < (StashHelperConfig.stashValueThreshold * 1_000_000)) {
                worthlessItems?.add(slot)
            }
        }
    }

    @Subscription
    fun onRender(event: RenderSlotEvent.After) {
        if (!StashHelperConfig.stashHelper) return
        if (event.slot !in (worthlessItems ?: return)) return

        event.graphics.fill(event.slot.x, event.slot.y, event.slot.x + 16, event.slot.y + 16, 0x88FF0000.toInt())
    }

    @Subscription
    fun onClick(event: SlotClickEvent) {
        if (!StashHelperConfig.blockUseless) return
        if (MC.instance.hasControlDown()) return
        if (event.slot !in (worthlessItems ?: return)) return
        event.cancel()
    }
}