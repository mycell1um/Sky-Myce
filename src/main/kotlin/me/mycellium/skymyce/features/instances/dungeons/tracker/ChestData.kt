package me.mycellium.skymyce.features.instances.dungeons.tracker

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.mycellium.skymyce.config.instances.dungeons.DungeonTrackerConfig
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonTracker.TrackedItem
import me.mycellium.skymyce.utils.ItemUtils.getPrice
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.toMutableList
import kotlin.collections.toMutableMap

data class ChestData(
    var chestCount: Int = 0,
    var chestCost: Double = 0.0,
    var rerollCount: Int = 0,
    var rerollCost: Double = 0.0,
    var valuables: MutableMap<SkyBlockId, MutableList<Long>> = mutableMapOf(),
    var trackedItems: MutableMap<SkyBlockId, TrackedItem> = mutableMapOf(),
) {
    val grossProfit: Double
        get() = trackedItems.values.sumOf { it.value }

    val netProfit: Double
        get() = grossProfit - chestCost - rerollCost

    fun openChest(items: Map<SkyBlockId, Int>, cost: Long) {
        val now = System.currentTimeMillis()
        chestCount++
        chestCost += cost
        items.forEach { (id, count) ->
            val value = (id.getPrice(DungeonTrackerConfig.bazaarPriceType, DungeonTrackerConfig.auctionPriceType)) * count

            if (value >= (DungeonTrackerConfig.valuableItemThreshold * 1_000_000)) valuables.getOrPut(id) { mutableListOf() }.add(now)

            val tracked = trackedItems.getOrPut(id) { TrackedItem() }
            tracked.value += value
            tracked.count += count
        }
    }

    companion object {
        val CODEC: Codec<ChestData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("chestCount").forGetter(ChestData::chestCount),
                Codec.DOUBLE.fieldOf("chestCost").forGetter(ChestData::chestCost),
                Codec.INT.fieldOf("rerollCount").forGetter(ChestData::rerollCount),
                Codec.DOUBLE.fieldOf("rerollCost").forGetter(ChestData::rerollCost),
                Codec.unboundedMap(
                    SkyBlockId.CODEC,
                    Codec.LONG.listOf().xmap({ it.toMutableList() }, { it })
                ).xmap({ it.toMutableMap() }, { it }).fieldOf("valuables").forGetter(ChestData::valuables),
                Codec.unboundedMap(
                    SkyBlockId.CODEC,
                    TrackedItem.CODEC
                ).xmap({ it.toMutableMap() }, { it }).fieldOf("trackedItems").forGetter(ChestData::trackedItems)
            ).apply(instance, ::ChestData)
        }
    }
}