package me.mycellium.skymyce.features.instances.dungeons.tracker

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.DungeonChestOpenEvent
import me.mycellium.skymyce.api.events.DungeonChestRerollEvent
import me.mycellium.skymyce.config.instances.dungeons.DungeonTrackerConfig
import me.mycellium.skymyce.utils.ItemUtils.getPrice
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonClass
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.dungeon.DungeonEnterEvent
import tech.thatgravyboat.skyblockapi.api.events.dungeon.DungeonStartEvent
import tech.thatgravyboat.skyblockapi.api.events.party.DungeonPartyFinderQueueEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import java.io.File
import java.text.NumberFormat
import java.util.*
import kotlin.jvm.optionals.getOrNull

object DungeonTracker : SkyMyceModule() {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    val saveFile: File = SkyMyce.configPath.resolve("dungeon_tracker.json").toFile()
    val CODEC: Codec<MutableMap<DungeonFloor, FloorData>> =
        Codec.unboundedMap(
            Codec.STRING.xmap({ DungeonFloor.valueOf(it) }, { it.name }),
            FloorData.CODEC
        ).xmap({ it.toMutableMap() }, { it })

    val profitData: MutableMap<DungeonFloor, FloorData> by lazy {
        if (!saveFile.exists()) return@lazy mutableMapOf()

        val json = saveFile.reader().use {
            gson.fromJson(it, JsonElement::class.java)
        }

        SkyMyce.logger.info("Loaded dungeon tracker data: {}", json)

        CODEC.parse(JsonOps.INSTANCE, json).result().orElse(mutableMapOf())
    }

    fun save() {
        val json = CODEC.encodeStart(JsonOps.INSTANCE, profitData).result().getOrNull() ?: return

        saveFile.parentFile.mkdirs()
        saveFile.writer().use {
            gson.toJson(json, it)
        }
    }

    private val floorRegex = Regex("^\\s*((?:Master Mode )?The Catacombs - (?:Floor ([IVX]+)|Entrance))$")
    private val xpRegex = Regex("^\\s*\\+([\\d,.]+) (.+) Experience(?: \\(Team Bonus\\))?$")

    private var endMessageCount = 0
    private var dungeonStartTime = 0L

    var currentFloor: DungeonFloor? = null

    @Subscription
    fun onDungeonQueue(event: DungeonPartyFinderQueueEvent) {
        currentFloor = event.floor
    }

    @Subscription
    fun onDungeonEnter(event: DungeonEnterEvent) {
        currentFloor = event.floor
    }

    @Subscription
    fun onDungeonStart(event: DungeonStartEvent) {
        endMessageCount = 0
        dungeonStartTime = System.currentTimeMillis()
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onChatMessage(event: ChatReceivedEvent.Pre) {
        if (!DungeonTrackerConfig.dungeonTracker) return
        if (endMessageCount > 1) return

        val floorData = profitData.getOrPut(DungeonAPI.dungeonFloor ?: return) { FloorData() }

        floorRegex.find(event.text)?.let {
            endMessageCount++
            floorData.totalRuns++
            floorData.totalTimeMillis += System.currentTimeMillis() - dungeonStartTime
            save()
        }

        xpRegex.find(event.text)?.let {
            val amount = NumberFormat.getNumberInstance(Locale.US).parse(it.groupValues[1]).toDouble()
            val type = it.groupValues[2]
            when {
                type == "Catacombs" -> {
                    floorData.totalXp += amount
                }

                else -> {
                    DungeonClass.getByName(type)?.let { clazz ->
                        floorData.classXp[clazz] = floorData.classXp.getOrPut(clazz) { 0.0 } + amount
                    }
                }
            }
            save()
        }
    }

    @Subscription
    fun onChestReroll(event: DungeonChestRerollEvent) {
        if (!DungeonTrackerConfig.dungeonTracker) return
        currentFloor = event.floor
        val chest = profitData.getOrPut(event.floor) { FloorData() }.chests.getOrPut(event.chest) { ChestData() }
        chest.rerollCount++
        chest.rerollCost += SkyBlockId.item("KISMET_FEATHER").getPrice(DungeonTrackerConfig.bazaarPriceType, DungeonTrackerConfig.auctionPriceType)
        save()
    }

    @Subscription
    fun onChestOpen(event: DungeonChestOpenEvent) {
        if (!DungeonTrackerConfig.dungeonTracker) return
        currentFloor = event.floor
        val chest = profitData.getOrPut(event.floor) { FloorData() }.chests.getOrPut(event.chest) { ChestData() }
        chest.openChest(event.contents, event.cost)
        save()
    }

    data class TrackedItem(
        var value: Double = 0.0,
        var count: Int = 0
    ) {
        companion object {
            val CODEC: Codec<TrackedItem> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.DOUBLE.fieldOf("value").forGetter(TrackedItem::value),
                    Codec.INT.fieldOf("count").forGetter(TrackedItem::count)
                ).apply(instance, ::TrackedItem)
            }
        }
    }
}