package me.mycellium.skymyce.features.general

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.RenderSlotEvent
import me.mycellium.skymyce.config.misc.LoadoutConfig
import me.mycellium.skymyce.utils.MC
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.utils.Scheduling.schedule
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.collections.iterator
import kotlin.time.Duration.Companion.seconds

object LoadoutKeybinds : SkyMyceModule() {

    private const val EQUIP_TEXT = "Left-click to equip!"
    private const val EMPTY_TEXT = "You must customize this loadout"

    private var pendingAutoClose = false
    private var lastClick = 0L

    private val loadoutRegex = Regex("^\\((\\d+)/(\\d+)\\) Loadouts$")
    private val loadoutIndices = mapOf(
        0 to 14, 1 to 15, 2 to 16,
        3 to 23, 4 to 24, 5 to 25,
        6 to 32, 7 to 33, 8 to 34,
        9 to 41, 10 to 42, 11 to 43
    )

    private val loadoutKeys: Map<Int, Int>
        get() {
            return mapOf(
                LoadoutConfig.slot1 to 0,
                LoadoutConfig.slot2 to 1,
                LoadoutConfig.slot3 to 2,
                LoadoutConfig.slot4 to 3,
                LoadoutConfig.slot5 to 4,
                LoadoutConfig.slot6 to 5,
                LoadoutConfig.slot7 to 6,
                LoadoutConfig.slot8 to 7,
                LoadoutConfig.slot9 to 8,
                LoadoutConfig.slot10 to 9,
                LoadoutConfig.slot11 to 10,
                LoadoutConfig.slot12 to 11,
            )
        }

    private var previousSlot: Slot? = null
    private var nextSlot: Slot? = null
    private var equippedSlot: Slot? = null

    private fun clearState() {
        previousSlot = null
        nextSlot = null
        equippedSlot = null
    }

    @Subscription
    fun onOpen(event: ContainerInitializedEvent) {
        clearState()

        if (!loadoutRegex.matches(event.title)) return

        if (pendingAutoClose) {
            pendingAutoClose = false
            MC.player.closeContainer()
            return
        }

        previousSlot = event.containerSlots.first {
            it.item.hoverName.stripped == "Previous Page"
        }

        nextSlot = event.containerSlots.first {
            it.item.hoverName.stripped == "Next Page"
        }

        for (index in loadoutIndices) {
            val slot = event.containerSlots.getOrNull(index.value) ?: continue

            val lore = slot.item.get(DataComponents.LORE)?.lines.orEmpty()

            val canEquip = lore.any { it.stripped == EQUIP_TEXT }
            val isEmpty = (slot.item.item == Items.RED_DYE || lore.any { it.stripped == EMPTY_TEXT })


            if (!canEquip && !isEmpty) {
                equippedSlot = slot
            }
        }
    }

    @Subscription
    fun onClose(event: ContainerCloseEvent) {

    }

    @Subscription
    fun onRenderSlot(event: RenderSlotEvent.Before) {
        if (!LoadoutConfig.enabled) return

        if (event.slot == equippedSlot) {
            event.graphics.fill(event.slot.x, event.slot.y, event.slot.x + 16, event.slot.y + 16, 0xFF55FF55.toInt())
        }
    }

    @Subscription
    fun onKey(event: ScreenKeyPressedEvent) {
        if (!LoadoutConfig.enabled) return
        if (!loadoutRegex.matches(event.screen.title.stripped)) return
        if (System.currentTimeMillis() - lastClick < 300) return

        val screen = event.screen as? AbstractContainerScreen<*> ?: return

        if (event.key == GLFW.GLFW_KEY_A) {
            previousSlot?.click(screen.menu)
        }

        if (event.key == GLFW.GLFW_KEY_D) {
            nextSlot?.click(screen.menu)
        }

        lastClick = System.currentTimeMillis()

        val loadout = loadoutKeys[event.key] ?: return
        val slotIndex = loadoutIndices[loadout] ?: return
        val slot = screen.menu.slots.getOrNull(slotIndex) ?: return
        slot.click(screen.menu)

        if (LoadoutConfig.autoClose) {
            closeAfterReopen()
        }
    }

    fun Slot.click(menu: AbstractContainerMenu) {
        MC.instance.gameMode?.handleContainerInput(
            menu.containerId,
            index,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            ContainerInput.PICKUP,
            MC.player
        )
    }

    fun closeAfterReopen() {
        MC.player.closeContainer()
        pendingAutoClose = true
        schedule(3.seconds) { pendingAutoClose = false }
    }
}