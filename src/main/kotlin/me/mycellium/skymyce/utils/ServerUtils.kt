package me.mycellium.skymyce.utils

import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.PacketEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import kotlin.math.min

object ServerUtils : SkyMyceModule() {
    var tps = 20f
        private set

    var currentPing = 0L
        private set

    var averagePing = 0L
        private set

    val serverTime: Long
        get() = System.currentTimeMillis() - currentPing

    private var lastTimePacket = 0L
    private var pingStartTime = 0L
    private var isPinging = false
    private var tickCounter = 0

    @Subscription
    fun onTick(event: TickEvent) {
        if (isPinging && Util.getNanos() - pingStartTime > 10_000_000_000L) {
            isPinging = false
        }

        tickCounter ++
        if (tickCounter >= 80) {
            tickCounter = 0
            sendPingRequest()
        }
    }

    @Subscription
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (event.packet is ClientboundSetTimePacket) {
            val now = System.currentTimeMillis()
            if (lastTimePacket != 0L) {
                val diff = now - lastTimePacket
                tps = (20_000f / diff).coerceIn(0f, 20f)
            }
            lastTimePacket = now
        }
        else if (packet is ClientboundPongResponsePacket) {
            currentPing = (Util.getMillis() - packet.time).coerceAtLeast(0)
            isPinging = false

            val pingLog = MC.instance.debugOverlay.pingLogger
            val sampleSize = min(pingLog.size(), 10)

            if (sampleSize > 0) {
                var total = 0L
                for (i in 0 until sampleSize) total += pingLog.get(i)
                averagePing = total / sampleSize
            }
            else averagePing = currentPing
        }
    }

    private fun sendPingRequest() {
        if (isPinging) return
        val connection = MC.connection ?: return

        isPinging = true
        pingStartTime = Util.getNanos()
        connection.send(ServerboundPingRequestPacket(Util.getMillis()))
    }
}