package me.mycellium.skymyce.features.general

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.misc.LoadoutConfig
import me.mycellium.skymyce.config.misc.WardrobeConfig
import me.mycellium.skymyce.utils.MC
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.ArmorWardrobeAPI

object WardrobeKeybinds : SkyMyceModule() {

    private val slots = mapOf(0 to 36, 1 to 37, 2 to 38, 3 to 39, 4 to 40, 5 to 41, 6 to 42, 7 to 43, 8 to 44)

    private val keys: Map<Int, Int>
        get() {
            return mapOf(
                WardrobeConfig.slot1 to 0,
                WardrobeConfig.slot2 to 1,
                WardrobeConfig.slot3 to 2,
                WardrobeConfig.slot4 to 3,
                WardrobeConfig.slot5 to 4,
                WardrobeConfig.slot6 to 5,
                WardrobeConfig.slot7 to 6,
                WardrobeConfig.slot8 to 7,
                WardrobeConfig.slot9 to 8,
                WardrobeConfig.slot10 to 9,
                WardrobeConfig.slot11 to 10,
                WardrobeConfig.slot12 to 11,
            )
        }

    @Subscription
    fun containerInit(event: ContainerInitializedEvent) {
    }

    @Subscription
    fun onKey(event: ScreenKeyPressedEvent) {
        if (!LoadoutConfig.enabled) return
        if (!ArmorWardrobeAPI.inWardrobe) return

        val screen = event.screen as? AbstractContainerScreen<*> ?: return
        val loadout = keys[event.key] ?: return
        val slotIndex = slots[loadout] ?: return
        val slot = screen.menu.slots.getOrNull(slotIndex) ?: return
        slot.click(screen.menu, GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
    }

    private fun Slot.click(menu: AbstractContainerMenu, type: Int) {
        MC.instance.gameMode?.handleContainerInput(
            menu.containerId,
            index,
            type,
            ContainerInput.PICKUP,
            MC.player
        )
    }
}