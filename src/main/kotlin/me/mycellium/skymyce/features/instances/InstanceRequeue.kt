package me.mycellium.skymyce.features.instances

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.ChatChannel
import me.mycellium.skymyce.config.instances.InstancesConfig
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.PlayerUtils
import me.mycellium.skymyce.utils.PlayerUtils.sendModMessage
import me.mycellium.skymyce.utils.Utils.displayModMessage
import me.mycellium.skymyce.utils.Utils.displayTitle
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import kotlin.time.Duration.Companion.seconds

object InstanceRequeue : SkyMyceModule() {
    private val dungeonEndRegex = Regex("^\\s*> EXTRA STATS <$")
    private val kuudraEndRegex = Regex("^\\s*(KUUDRA DOWN!|DEFEAT)$")
    private val requeueRegex = Regex("^You have been re-queued!$")
    private val notCreatorRegex = Regex("^Only the instance creator can re-queue!$")

    private val downtimeReason = mutableMapOf<String, String>()

    private var attemptRequeue = false
    private var retryCount = 0

    private const val MAX_RETRIES = 10


    private fun startRequeue() {
        if (attemptRequeue) return

        attemptRequeue = true
        retryCount = 0

        Scheduling.schedule(InstancesConfig.requeueDelay.seconds) {
            tryRequeue()
        }
    }

    private fun tryRequeue() {
        if (!attemptRequeue) return
        if (PartyAPI.leader?.name != MC.instance.gameProfile.name) return

        if (retryCount >= MAX_RETRIES) {
            displayModMessage("§bFailed to requeue instance after $MAX_RETRIES attempts.")
            cancelRequeue()
            return
        }

        retryCount++

        PlayerUtils.sendCommand("instancerequeue", true)
    }

    private fun cancelRequeue() {
        attemptRequeue = false
        retryCount = 0
    }

    @Subscription
    @TimePassed("20t")
    fun onTick(event: TickEvent) {
        if (!InstancesConfig.autoRequeue) return
        tryRequeue()
    }

    @Subscription
    fun onIslandChange(event: IslandChangeEvent) {
        cancelRequeue()
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (!InstancesConfig.autoRequeue) return

        if (requeueRegex.matches(event.text) || notCreatorRegex.matches(event.text)) {
            cancelRequeue()
            return
        }

        val runEnded = when (LocationAPI.island) {
            SkyBlockIsland.THE_CATACOMBS ->
                dungeonEndRegex.matches(event.text)

            SkyBlockIsland.KUUDRA ->
                kuudraEndRegex.matches(event.text)

            else -> false
        }

        if (!runEnded) return

        if (downtimeReason.isNotEmpty()) {
            displayTitle("§b§lREQUEUE CANCELED", "§7Downtime requested")

            Scheduling.schedule(1.seconds) {
                sendModMessage("Downtime needed: ${downtimeReason.keys.joinToString(", ")}", ChatChannel.PARTY)
                downtimeReason.forEach { (player, reason) ->
                    displayModMessage("§b§l$player§r§7 requested downtime: §e$reason")
                }
                downtimeReason.clear()
            }
            return
        }

        startRequeue()
    }

    fun requestDowntime(player: String, reason: String) {
        downtimeReason[player] = reason

        displayTitle("§c§lDOWNTIME", "§7$player${if (reason.isBlank()) "" else " -> $reason"}")
        displayModMessage("§7Downtime requested, auto requeue cancelled")
    }
}