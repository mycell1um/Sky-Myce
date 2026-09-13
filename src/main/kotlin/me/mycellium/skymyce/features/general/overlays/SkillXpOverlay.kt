package me.mycellium.skymyce.features.general.overlays

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.misc.GeneralConfig
import me.mycellium.skymyce.hud.elements.ItemElement
import me.mycellium.skymyce.hud.elements.LayoutAxis
import me.mycellium.skymyce.hud.elements.LayoutType
import me.mycellium.skymyce.hud.elements.StackElement
import me.mycellium.skymyce.hud.widget.Widget
import me.mycellium.skymyce.hud.elements.TextElement
import me.mycellium.skymyce.hud.widget.Anchor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.RenderActionBarWidgetEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpLiteralActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SkillXpPercentActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object SkillXpOverlay : SkyMyceModule() {
    private var display = ""
    private var skill: Skill? = null
    private var timeSince: Duration = 0.milliseconds

    val widget = Widget("skill_xp_display", "Skill Xp Display", 480, 290, anchor = Anchor.CENTER) {
        if (!GeneralConfig.displayXp) return@Widget null

        val now = System.currentTimeMillis().milliseconds
        if (now - timeSince > 5.seconds) return@Widget null

        return@Widget StackElement(LayoutAxis.HORIZONTAL, LayoutType.CENTER, 5,
            listOf(
                ItemElement(ItemStack(skill?.skillItem() ?: Items.BARRIER)),
                TextElement(display)
            )
        )
    }

    @Subscription
    fun onWidgetRender(event: RenderActionBarWidgetEvent) {
        if (!GeneralConfig.displayXp) return

        if (event.widget == ActionBarWidget.SKILL_XP || event.widget == ActionBarWidget.SKILL_XP_LITERAL) {
            event.cancel()
        }
    }

    @Subscription
    fun onWidgetEvent(event: ActionBarWidgetChangeEvent) {
        when (event) {
            is SkillXpPercentActionBarWidgetChangeEvent -> {
                display = event.new
                skill = event.skill
                timeSince = System.currentTimeMillis().milliseconds
            }

            is SkillXpLiteralActionBarWidgetChangeEvent -> {
                display = event.new
                skill = event.skill
                timeSince = System.currentTimeMillis().milliseconds
            }
        }
    }

    fun Skill.skillItem(): Item = when(this) {
        Skill.COMBAT -> Items.STONE_SWORD
        Skill.FARMING -> Items.GOLDEN_HOE
        Skill.FISHING -> Items.FISHING_ROD
        Skill.MINING -> Items.STONE_PICKAXE
        Skill.FORAGING -> Items.JUNGLE_SAPLING
        Skill.ENCHANTING -> Items.ENCHANTING_TABLE
        Skill.ALCHEMY -> Items.BREWING_STAND
        Skill.CARPENTRY -> Items.CRAFTING_TABLE
        Skill.RUNECRAFTING -> Items.MAGMA_CREAM
        Skill.TAMING -> Items.POLAR_BEAR_SPAWN_EGG
        Skill.SOCIAL -> Items.EMERALD
        Skill.HUNTING -> Items.LEAD
    } ?: Items.BARRIER
}