package me.mycellium.skymyce.features.instances.dungeons.tracker

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.mycellium.skymyce.api.DungeonChest
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonClass
import kotlin.collections.toMutableMap
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

data class FloorData(
    var totalRuns: Int = 0,
    var totalTimeMillis: Long = 0L,
    var totalXp: Double = 0.0,
    var classXp: MutableMap<DungeonClass, Double> = mutableMapOf(),
    var chests: MutableMap<DungeonChest, ChestData> = mutableMapOf(),
) {
    val totalChestsOpened: Int
        get() = chests.values.sumOf { it.chestCount }

    val totalChestsCost: Double
        get() = chests.values.sumOf { it.chestCost }

    val totalRerolled: Int
        get() = chests.values.sumOf { it.rerollCount }

    val totalRerolledCost: Double
        get() = chests.values.sumOf { it.rerollCost }

    val netProfit: Double
        get() = chests.values.sumOf { it.netProfit }

    val grossProfit: Double
        get() = chests.values.sumOf { it.grossProfit }

    val totalTimeHours: Double
        get() = totalTimeMillis.milliseconds.toDouble(DurationUnit.HOURS)

    companion object {
        val CODEC: Codec<FloorData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("totalRuns").forGetter(FloorData::totalRuns),
                Codec.LONG.fieldOf("totalTimeMillis").forGetter(FloorData::totalTimeMillis),
                Codec.DOUBLE.fieldOf("totalXp").forGetter(FloorData::totalXp),
                Codec.unboundedMap(
                    Codec.STRING.xmap({ DungeonClass.valueOf(it) }, { it.name }),
                    Codec.DOUBLE
                ).xmap({ it.toMutableMap() }, { it }).fieldOf("classXp").forGetter(FloorData::classXp),
                Codec.unboundedMap(
                    Codec.STRING.xmap({ DungeonChest.valueOf(it) }, { it.name }),
                    ChestData.CODEC
                ).xmap({ it.toMutableMap() }, { it }).fieldOf("chests").forGetter(FloorData::chests),
            ).apply(instance, ::FloorData)
        }
    }
}