package me.mycellium.skymyce.features.general.overlays

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.misc.GeneralConfig
import me.mycellium.skymyce.hud.HudElement
import me.mycellium.skymyce.hud.elements.LayoutAxis
import me.mycellium.skymyce.hud.elements.LayoutType
import me.mycellium.skymyce.hud.elements.StackElement
import me.mycellium.skymyce.hud.elements.TextElement
import me.mycellium.skymyce.hud.widget.Anchor
import me.mycellium.skymyce.hud.widget.Widget
import me.mycellium.skymyce.utils.PlayerUtils
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object AbilityCooldownOverlay : SkyMyceModule() {
    private val cooldowns: MutableMap<SkyBlockId, Triple<Duration, Duration, Duration>> = mutableMapOf()

    val widget = Widget("ability_cooldown_display", "Ability Cooldown Display", 480, 270, anchor = Anchor.CENTER) {
        if (!GeneralConfig.displayAbilityCooldown) return@Widget null
        val now = System.currentTimeMillis().milliseconds

        val id = PlayerUtils.activeItem.getSkyBlockId() ?: return@Widget null
        val cooldown = cooldowns[id] ?: return@Widget null

        val time = now - cooldown.first
        val duration = maxOf(cooldown.second, cooldown.third)

        return@Widget if (time < duration) {
            TextElement("§cCooldown: ${(duration - time).inWholeSeconds.seconds}")
        } else {
            TextElement("§aAbility Ready!")
        }
    }

    val notiWidget = Widget("ability_reset_display", "Ability Reset Display", 480, 270, 1.5f, anchor = Anchor.CENTER) {
        if (!GeneralConfig.displayAbilityNotification) return@Widget null
        val now = System.currentTimeMillis().milliseconds

        val lines: MutableList<HudElement> = mutableListOf()
        for ((id, cooldown) in cooldowns) {
            val time = now - cooldown.first
            val duration = maxOf(cooldown.second, cooldown.third)

            if ((time - duration) !in 0.seconds..5.seconds) continue

            lines += TextElement("§a${id.toItem().hoverName.stripped}")
        }

        if (lines.isEmpty()) return@Widget null

        return@Widget StackElement(LayoutAxis.VERTICAL, LayoutType.CENTER, children = lines)
    }

    @Subscription
    fun onItemRightClick(event: RightClickEvent) {
        addCooldown(event.stack)
    }

    private fun addCooldown(item: ItemStack) {
        val id = item.getSkyBlockId() ?: return
        val now = System.currentTimeMillis().milliseconds

        val cooldown = when {
            item.getData(DataTypes.NECRON_SCROLLS)?.contains("WITHER_SHIELD_SCROLL") ?: false -> 5.seconds
            else -> item.getData(DataTypes.COOLDOWN_ABILITY)?.second
        } ?: return

        val duration = when (item.getData(DataTypes.ID)) {
            "RAGNAROCK_AXE" -> 10.seconds
            "WEIRD_TUBA" -> 20.seconds
            "WEIRDER_TUBA" -> 30.seconds
            "FIRE_VEIL_WAND" -> 5.seconds
            "WAND_OF_HEALING", "WAND_OF_RESTORATION", "WAND_OF_MENDING", "WAND_OF_ATONEMENT" -> 7.seconds
            else -> cooldown
        }

        cooldowns[id]?.let {
            if ((now - it.first) < cooldown) return
        }

        cooldowns[id] = Triple(now, cooldown, duration)
    }
}