package me.mycellium.skymyce.features.instances.dungeons.tracker

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.instances.dungeons.DungeonTrackerConfig
import me.mycellium.skymyce.hud.HudElement
import me.mycellium.skymyce.hud.elements.PanelElement
import me.mycellium.skymyce.hud.elements.StackElement
import me.mycellium.skymyce.hud.elements.TextElement
import me.mycellium.skymyce.hud.widget.Widget
import me.mycellium.skymyce.utils.NumberUtils
import me.mycellium.skymyce.utils.TextUtils
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import kotlin.collections.get
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object DungeonTrackerWidget : SkyMyceModule() {
    val widget = Widget("dungeon_tracker", "Dungeon Tracker", 200, 100) {
        if (!DungeonTrackerConfig.dungeonTrackerWidget) return@Widget null
        if (!SkyBlockIsland.inAnyIsland(SkyBlockIsland.DUNGEON_HUB, SkyBlockIsland.THE_CATACOMBS)) return@Widget null
        if (DungeonAPI.started && !DungeonAPI.completed) return@Widget null

        val stats = DungeonTracker.profitData[DungeonTracker.currentFloor]
        val title = DungeonTracker.currentFloor?.name ?: "?"

        val lines: MutableList<HudElement> = mutableListOf()

        lines += TextElement("§b§l$title Dungeon Tracker")
        when {
            (DungeonTracker.currentFloor == null) -> {
                lines += TextElement("§7Unknown floor!")
                lines += TextElement("§8(Try queueing a party)")
            }

            (stats == null) -> {
                lines += TextElement("§7No dungeon data yet.")
                lines += TextElement("§8(Try playing runs)")
            }

            else -> {
                lines += TextElement("§8/skymyce dungeon")
                lines += TextElement("")
                lines += TextElement(
                    "§7Time: §e${stats.totalTimeMillis.milliseconds.inWholeSeconds.seconds}§7 (§e${
                        (stats.totalTimeMillis / stats.totalRuns.coerceAtLeast(
                            1
                        )).milliseconds.inWholeSeconds.seconds
                    }§7/run)"
                )
                lines += TextElement("§7Runs: §a${stats.totalRuns}§7 (§a${round((stats.totalRuns / stats.totalTimeHours) * 100) / 100}§7/h)")
                lines += TextElement("§7Chests: §a${stats.totalChestsOpened}§7 (§b${stats.totalRerolled} rerolls§7)")
                lines += TextElement("")
                lines += TextElement("§7Profit: ${TextUtils.parseProfit(stats.netProfit)}§7 (${TextUtils.parseProfit(stats.netProfit / stats.totalTimeHours)}§7/h)")

                lines += TextElement(
                    "§7Cata Exp: §b${NumberUtils.condense(stats.totalXp)} §7(§b${
                        NumberUtils.condense(
                            stats.totalXp / stats.totalTimeHours
                        )
                    }§7/h)"
                )
            }
        }

        return@Widget PanelElement(StackElement(children = lines), padding = 6)
    }
}