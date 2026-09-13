package me.mycellium.skymyce.features.instances.dungeons.tracker

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.UIComponents
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.ScrollContainer
import io.wispforest.owo.ui.container.UIContainers
import io.wispforest.owo.ui.core.*
import me.mycellium.skymyce.api.DungeonChest
import me.mycellium.skymyce.api.DungeonChest.Companion.formatted
import me.mycellium.skymyce.utils.NumberUtils
import me.mycellium.skymyce.utils.TextUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class DungeonScreen : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> = OwoUIAdapter.create(this, UIContainers::verticalFlow)

    override fun build(root: FlowLayout) {
        root.surface(Surface.blur(3.0f, 10.0f))
        root.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        root.child(mainLayout())
    }

    private fun mainLayout(): UIComponent {
        val floors = DungeonTracker.profitData.keys.sortedBy { it.ordinal }
        val currentIndex = floors.indexOf(DungeonTracker.currentFloor)

        val layout = UIContainers.verticalFlow(Sizing.fill(80), Sizing.fill(80))
        layout.surface(Surface.DARK_PANEL)
        layout.padding(Insets.of(10))
        layout.gap(5)
        layout.child(
            UIContainers.horizontalFlow(Sizing.fill(), Sizing.content())
                .child(UIComponents.button(Component.literal("§l<")) {
                    selectFloor(floors[(currentIndex - 1) % floors.size])
                }.sizing(Sizing.fixed(20)))
                .child(UIComponents.label(Component.literal("§b§l${DungeonTracker.currentFloor?.name} Tracker Data")))
                .child(UIComponents.button(Component.literal("§l>")) {
                    selectFloor(floors[(currentIndex + 1) % floors.size])
                }.sizing(Sizing.fixed(20)))
                .gap(5)
                .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        )
        layout.child(
            UIContainers.horizontalFlow(Sizing.expand(), Sizing.expand())
                .child(
                    UIContainers.verticalFlow(Sizing.fill(20), Sizing.fill())
                        .child(UIComponents.label(Component.literal("§bInfo")))
                        .child(floorPane())
                        .gap(5)
                )
                .child(
                    UIContainers.verticalFlow(Sizing.expand(), Sizing.expand())
                        .child(
                            UIContainers.grid(Sizing.fill(), Sizing.content(), 1, 4)
                                .child(UIComponents.label(Component.literal("§bName")), 0, 0)
                                .child(UIComponents.label(Component.literal("§bCount")), 0, 1)
                                .child(UIComponents.label(Component.literal("§bValue")), 0, 2)
                                .child(UIComponents.label(Component.literal("§bDrop Chance")), 0, 3)
                                .padding(Insets.of(0, 0, 3, 3))
                        )
                        .child(itemScroll())
                        .child(UIComponents.label(Component.literal("§bRNG Timeline")))
                        .child(timelinePanel()).gap(5)
                ).gap(5)
        )

        return layout
    }

    private fun floorPane(): UIComponent {
        val content = UIContainers.verticalFlow(Sizing.content(), Sizing.content())
        content.padding(Insets.of(3))
        content.gap(3)
        content.surface(Surface.flat(0xFF333333.toInt()))

        val stats = DungeonTracker.profitData[DungeonTracker.currentFloor]
        if (stats != null) {
            val chestContainer = UIContainers.collapsible(
                Sizing.content(),
                Sizing.content(),
                Component.literal("§7Chests: §a${stats.totalChestsOpened}§7 (§b${stats.totalRerolled}§7)"),
                true
            )
            stats.chests.toSortedMap(compareBy { it.ordinal }).forEach { (chest, data) ->
                chestContainer.child(
                    UIComponents.label(Component.literal("${chest.formatted()}§7: §a${data.chestCount}§7 (§b${data.rerollCount}§7)"))
                        .tooltip(
                            listOf(
                                Component.literal("§7RNG Drops: §d${data.valuables.values.sumOf { it.size }}"),
                                Component.literal("§7Net Profit: ${TextUtils.parseProfit(data.netProfit)}"),
                                Component.literal("§7Gross Profit: ${TextUtils.parseProfit(data.grossProfit)}"),
                                Component.literal("§7Chest Cost: ${TextUtils.parseProfit(-data.chestCost)}"),
                                Component.literal("§7Reroll Cost: ${TextUtils.parseProfit(-data.rerollCost)}")
                            )
                        )
                )
            }

            val xpContainer = UIContainers.collapsible(
                Sizing.content(),
                Sizing.content(),
                Component.literal("§7Cata Xp: §b${NumberUtils.condense(stats.totalXp)}"),
                true
            )
            xpContainer.tooltip(Component.literal("§7Cata Xp/hour: §b${NumberUtils.condense(stats.totalXp / stats.totalTimeHours)}"))
            stats.classXp.forEach { (clazz, amount) ->
                xpContainer.child(
                    UIComponents.label(Component.literal("§7${clazz.displayName}: §b${NumberUtils.condense(amount)}"))
                        .tooltip(
                            listOf(
                                Component.literal("§7${clazz.displayName} Xp/hour: §b${NumberUtils.condense(amount / stats.totalTimeHours)}")
                            )
                        )
                )
            }

            content.child(
                UIComponents.label(Component.literal("§7Time: §e${stats.totalTimeMillis.milliseconds.inWholeSeconds.seconds}§7"))
                    .tooltip(Component.literal("§e${(stats.totalTimeMillis / stats.totalRuns.coerceAtLeast(1)).milliseconds.inWholeSeconds.seconds}§7/run"))
            )
            content.child(
                UIComponents.label(
                    Component.literal(
                        "§7Profit: ${TextUtils.parseProfit(stats.netProfit)}§7 (${
                            TextUtils.parseProfit(
                                stats.netProfit / stats.totalTimeHours
                            )
                        }§7/h)"
                    )
                ).tooltip(
                    listOf(
                        Component.literal("§7Gross Profit: ${TextUtils.parseProfit(stats.grossProfit)}"),
                        Component.literal("§7Chest Cost: ${TextUtils.parseProfit(-stats.totalChestsCost)}"),
                        Component.literal("§7Reroll Cost: ${TextUtils.parseProfit(-stats.totalRerolledCost)}"),
                    )
                )
            )
            content.child(
                UIComponents.label(Component.literal("§7Runs: §a${stats.totalRuns}§7 (§a${round((stats.totalRuns / stats.totalTimeHours) * 100) / 100}§7/h)"))
            )
            content.child(chestContainer)
            content.child(xpContainer)
        }

        val scroll = UIContainers.verticalScroll(Sizing.expand(), Sizing.expand(), content)
        scroll.padding(Insets.of(3))
        scroll.scrollbar(ScrollContainer.Scrollbar.flat(Color.ofRgb(0xFFFFFF)))
        scroll.scrollbarThiccness(2)
        scroll.surface(Surface.flat(0xFF333333.toInt()))
        return scroll
    }

    private fun itemScroll(): UIComponent {
        val items = trackedItems()
        val floor = DungeonTracker.profitData[DungeonTracker.currentFloor]

        val content: UIComponent
        if (floor == null) {
            content = UIComponents.label(Component.literal("No data found on this floor."))
        } else {
            content = UIContainers.grid(Sizing.fill(), Sizing.content(), items.size, 4)
            content.alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER)
            content.allowOverflow(false)

            items.forEachIndexed { index, item ->
                content.child(
                    UIContainers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(UIComponents.item(item.id.toItem()).setTooltipFromStack(true))
                        .child(UIComponents.label(Component.literal(item.id.toItem().hoverName.string))).gap(5)
                        .alignment(HorizontalAlignment.LEFT, VerticalAlignment.CENTER), index, 0
                )
                content.child(
                    UIComponents.label(Component.literal(integerFormat.format(item.count))).color(COUNT_COLOR), index, 1
                )
                content.child(UIComponents.label(Component.literal("§a${NumberUtils.condense(item.profit)}")), index, 2)
                content.child(UIComponents.label(Component.literal("§9${round((item.count.toDouble() / (floor.totalChestsOpened + floor.totalRerolled).toDouble()) * 10000) / 100}%")), index, 3)
            }
        }

        val scroll = UIContainers.verticalScroll(Sizing.expand(), Sizing.expand(), content)
        scroll.padding(Insets.of(3))
        scroll.scrollbar(ScrollContainer.Scrollbar.flat(Color.ofRgb(0xFFFFFF)))
        scroll.scrollbarThiccness(2)
        scroll.surface(Surface.flat(0xFF333333.toInt()))
        return scroll
    }

    private fun timelinePanel(): UIComponent {
        val floor = DungeonTracker.profitData[DungeonTracker.currentFloor]
        val items = mutableListOf<TimelineItem>()
        floor?.chests?.forEach { (chest, data) ->
            data.valuables.forEach { (id, dates) ->
                dates.forEach { time ->
                    items.add(TimelineItem(id, time, chest))
                }
            }
        }

        val content = UIContainers.horizontalFlow(Sizing.content(), Sizing.content())
        content.gap(5)
        items.sortedBy { it.time }.forEach { timelineItem ->
            val item = timelineItem.id.toItem().copy()
            val time = Instant.ofEpochMilli(timelineItem.time).atZone(ZoneId.systemDefault())

            val lines = item.get(DataComponents.LORE)?.lines?.toMutableList() ?: mutableListOf()
            lines.addAll(
                listOf(
                    Component.literal("§8§m                                        "),
                    Component.literal("§7Obtained: §b${time.format(DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a"))}"),
                    Component.literal("§7Chest: ${timelineItem.chest.formatted()}")
                )
            )
            item.set(DataComponents.LORE, ItemLore(lines))

            content.child(
                UIComponents.item(item).setTooltipFromStack(true)
            )
        }

        val scroll = UIContainers.horizontalScroll(Sizing.fill(), Sizing.content(), content)
        scroll.padding(Insets.of(3))
        scroll.scrollbar(ScrollContainer.Scrollbar.flat(Color.ofRgb(0xFFFFFF)))
        scroll.scrollbarThiccness(2)
        scroll.surface(Surface.flat(0xFF333333.toInt()))
        return scroll
    }

    private data class TimelineItem(val id: SkyBlockId, val time: Long, val chest: DungeonChest)

    private fun selectFloor(floor: DungeonFloor) {
        DungeonTracker.currentFloor = floor
        uiAdapter.rootComponent.clearChildren()
        build(uiAdapter.rootComponent)
        uiAdapter.inflateAndMount()
    }

    private fun trackedItems(): List<TrackedItemRow> {
        val stats = DungeonTracker.profitData[DungeonTracker.currentFloor] ?: return emptyList()
        return stats.chests.values.flatMap { it.trackedItems.entries }.groupBy({ it.key }, { it.value })
            .map { (id, tracked) ->
                TrackedItemRow(id = id, count = tracked.sumOf { it.count }, profit = tracked.sumOf { it.value })
            }.sortedByDescending { it.profit }
    }

    private data class TrackedItemRow(
        val id: SkyBlockId,
        val count: Int,
        val profit: Double,
    )

    private companion object {
        val integerFormat: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)
        val TITLE_COLOR: Color = Color.ofRgb(0x55FFFF)
        val MUTED: Color = Color.ofRgb(0xAAAAAA)
        val COUNT_COLOR: Color = Color.ofRgb(0xFFFF55)
        val PROFIT_COLOR: Color = Color.ofRgb(0x55FF55)
    }
}