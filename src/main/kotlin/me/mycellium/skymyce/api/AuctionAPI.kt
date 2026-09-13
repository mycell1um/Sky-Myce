package me.mycellium.skymyce.api

import com.google.gson.JsonObject
import me.mycellium.skymyce.SkyMyceModule
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.legacyStack
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import java.io.ByteArrayInputStream
import java.util.Base64
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://api.hypixel.net/v2/skyblock/auctions"

object AuctionApi : SkyMyceModule() {

    var auctions = listOf<ActiveAuction>()
        private set

    init {
        Scheduling.schedule(0.seconds, 5.minutes) {
            Http.getResult<JsonObject>(URL).let { res ->
                val response = res.getOrNull() ?: return@schedule
                auctions = response.getAsJsonArray("auctions").map { auction ->
                    val obj = auction.asJsonObject

                    val bytes = Base64.getDecoder().decode(obj["item_bytes"].asJsonObject["data"].asString)
                    val nbt = NbtIo.readCompressed(ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap())

                    ActiveAuction(
                        obj["uuid"].asString,
                        obj["auctioneer"].asString,
                        obj["start"].asLong,
                        obj["end"].asLong,
                        obj["category"].asString,
                        obj["starting_bid"].asLong,
                        nbt.getList("i").getOrNull()?.mapNotNull { it.legacyStack() }?.get(0) ?: ItemStack.EMPTY,
                    )
                }
            }
        }
    }

    fun getData() {
        Scheduling.schedule(1.seconds) {
            Http.getResult<JsonObject>(URL).let { res ->
                val response = res.getOrNull() ?: return@schedule
                auctions = response.getAsJsonArray("auctions").map { auction ->
                    val obj = auction.asJsonObject

                    val bytes = Base64.getDecoder().decode(obj["item_bytes"].asJsonObject["data"].asString)
                    val nbt = NbtIo.readCompressed(ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap())

                    ActiveAuction(
                        obj["uuid"].asString,
                        obj["auctioneer"].asString,
                        obj["start"].asLong,
                        obj["end"].asLong,
                        obj["category"].asString,
                        obj["starting_bid"].asLong,
                        nbt.getList("i").getOrNull()?.mapNotNull { it.legacyStack() }?.get(0) ?: ItemStack.EMPTY,
                    )
                }
            }
        }
    }

    data class ActiveAuction(
        val uuid: String,
        val auctioneer: String,
        val start: Long,
        val end: Long,
        val category: String,
        val price: Long,
        val item: ItemStack
    ) {
        val duration: Long
            get() = end - start

        val remaining: Long
            get() = end - System.currentTimeMillis()

        val expired: Boolean
            get() = remaining <= 0
    }
}