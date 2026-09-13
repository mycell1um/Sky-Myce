package me.mycellium.skymyce.features.instances.dungeons

import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.DungeonRank
import me.mycellium.skymyce.api.DungeonRank.Companion.formatted
import me.mycellium.skymyce.config.instances.dungeons.DungeonsConfig
import me.mycellium.skymyce.utils.MC
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import kotlin.math.*

object DungeonWinOverlay : SkyMyceModule() {
    const val RANK_WIDTH = 96
    const val RANK_HEIGHT = 48

    val SCORE_REGEX = Regex("^\\s*Team Score: (\\d+) \\((.+?)\\)( \\(NEW RECORD!\\))?$")

    var showOverlay = false
    var startTime: Long = 0L
    var currentRank = DungeonRank.UNKNOWN
    var currentStats = mutableListOf<Component>()

    override fun init() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(SkyMyce.MOD_ID, "dungeon_win_screen")
        ) { context, _ -> render(context) }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onChatEvent(event: ChatReceivedEvent.Pre) {
        SCORE_REGEX.find(event.text)?.let {
            showOverlay = true
            startTime = System.currentTimeMillis()
            currentRank = DungeonRank.fromLabel(it.groupValues[2])
            currentStats = mutableListOf<Component>().apply {
                add(Component.literal("§c${DungeonAPI.dungeonFloor?.longName}"))
                add(Component.literal("§fTeam Score: §a${it.groupValues[1]}§f (${currentRank.formatted()}§f)"))
                add(Component.literal("§7Time Elapsed: §e${DungeonAPI.time}"))
            }
        }
    }

    @Subscription
    fun onIslandChange(event: IslandChangeEvent) {
        showOverlay = false
    }

    fun render(graphics: GuiGraphicsExtractor) {
        if (!DungeonsConfig.dungeonWinScreen) return
        if (!showOverlay) return
        if (MC.instance.options.hideGui) return

        val age = System.currentTimeMillis() - startTime

        val widgetX = graphics.guiWidth() / 2F
        val widgetY = when {
            age < 500 -> graphics.guiHeight() / 2f - sin(age / 1000f * PI).toFloat() * graphics.guiHeight() / 9f
            age < 1000 -> graphics.guiHeight() / 6f + sin(age / 1000f * PI).toFloat() * graphics.guiHeight() * 4f / 18f
            else -> graphics.guiHeight() / 6f
        }

        val scale = when {
            age < 200 -> max(0.05f, age / 200f)
            age < 1000 -> 1f + sin((age - 200) / 800f * PI).toFloat() * 0.5f
            age < 1250 -> 1f + sin((age - 1000) / 250f * PI).toFloat() * 0.25f
            else -> 1f
        }

        val rankWidth = (RANK_WIDTH * scale).roundToInt()
        val rankHeight = (RANK_HEIGHT * scale).roundToInt()

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            currentRank.texture,
            (widgetX + rankWidth / -2).toInt(),
            (widgetY + rankHeight / -2).toInt(),
            0f,
            0f,
            rankWidth,
            rankHeight,
            rankWidth,
            rankHeight
        )

        // Run info
        if (age > 650) {
            for ((index, line) in currentStats.withIndex()) {
                val lineAge = (age - 650) - index * 50
                if (lineAge < 0) continue
                val textAlpha = (min(1f, lineAge / 200f) * 255).roundToInt()
                val lineColor = (textAlpha shl 24) or 0xFFFFFF
                graphics.centeredText(
                    MC.font,
                    line,
                    widgetX.toInt(),
                    (widgetY.toInt() + RANK_HEIGHT / 2 + 15) + index * (MC.font.lineHeight + 3),
                    lineColor
                )
            }
        }
    }
}
