package me.mycellium.skymyce.features.general

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.ChatChannel
import me.mycellium.skymyce.api.events.PlayerMessageEvent
import me.mycellium.skymyce.config.misc.PartyCommandsConfig
import me.mycellium.skymyce.features.instances.InstanceRequeue
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonTracker
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.NumberUtils
import me.mycellium.skymyce.utils.PlayerUtils.sendCommand
import me.mycellium.skymyce.utils.PlayerUtils.sendMessage
import me.mycellium.skymyce.utils.PlayerUtils.sendModMessage
import me.mycellium.skymyce.utils.ServerUtils
import me.mycellium.skymyce.utils.Utils.displayDevMessage
import me.mycellium.skymyce.utils.Utils.displayModMessage
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object PartyCommands : SkyMyceModule() {
    private val commandRegex = Regex("^!(?<command>\\w+)\\s*(?<arguments>.*)$")
    private var cooldownTimestamp = 0.seconds

    val queueMap = mutableMapOf(
        "fe" to "CATACOMBS_ENTRANCE",
        "f1" to "CATACOMBS_FLOOR_ONE",
        "f2" to "CATACOMBS_FLOOR_TWO",
        "f3" to "CATACOMBS_FLOOR_THREE",
        "f4" to "CATACOMBS_FLOOR_FOUR",
        "f5" to "CATACOMBS_FLOOR_FIVE",
        "f6" to "CATACOMBS_FLOOR_SIX",
        "f7" to "CATACOMBS_FLOOR_SEVEN",
        "m1" to "MASTER_CATACOMBS_FLOOR_ONE",
        "m2" to "MASTER_CATACOMBS_FLOOR_TWO",
        "m3" to "MASTER_CATACOMBS_FLOOR_THREE",
        "m4" to "MASTER_CATACOMBS_FLOOR_FOUR",
        "m5" to "MASTER_CATACOMBS_FLOOR_FIVE",
        "m6" to "MASTER_CATACOMBS_FLOOR_SIX",
        "m7" to "MASTER_CATACOMBS_FLOOR_SEVEN",
        "t1" to "KUUDRA_NORMAL",
        "t2" to "KUUDRA_HOT",
        "t3" to "KUUDRA_BURNING",
        "t4" to "KUUDRA_FIERY",
        "t5" to "KUUDRA_INFERNAL",
    )

    @Subscription
    fun onPartyChat(event: PlayerMessageEvent) {
        if (event.type != ChatChannel.PARTY) return
        commandRegex.find(event.message)?.let {
            if (!PartyAPI.inParty) return@let

            val isLeader = PartyAPI.leader?.name == MC.instance.gameProfile.name

            val commandLiteral = it.groupValues[1]
            val argsLiteral = it.groupValues[2]

            val command = PartyCommandTypes.fromAlias(commandLiteral) ?: return@let
            val args = argsLiteral.split(" ")

            if (command !in PartyCommandsConfig.enabledPartyCommands) return@let

            val now = System.currentTimeMillis().milliseconds
            if (now - cooldownTimestamp < PartyCommandsConfig.partyCommandCooldown.seconds) return@let
            cooldownTimestamp = now

            displayDevMessage("leader: ${PartyAPI.leader?.name} ($isLeader)")
            when (command) {
                PartyCommandTypes.INVITE -> if (isLeader) sendCommand("party invite ${args[0]}", true)

                PartyCommandTypes.KICK -> if (isLeader) sendCommand("party kick ${findPartyMember(args[0])}", true)

                PartyCommandTypes.ALLINVITE -> {
                    if (isLeader) {
                        when (args[0]) {
                            "on", "yes" -> if (!PartyAPI.allInvite) sendCommand("party settings allinvite")
                            "off", "no" -> if (PartyAPI.allInvite) sendCommand("party settings allinvite")
                            else -> sendCommand("party settings allinvite")
                        }
                    }
                }

                PartyCommandTypes.DISBAND -> if (isLeader) sendCommand("party disband")

                PartyCommandTypes.WARP -> if (isLeader) sendCommand("party warp")

                PartyCommandTypes.TRANSFER -> if (isLeader) sendCommand("party transfer ${targetArgument(args[0], event.player)}", true)

                PartyCommandTypes.PROMOTE -> if (isLeader) sendCommand("party promote ${targetArgument(args[0], event.player)}", true)

                PartyCommandTypes.DEMOTE -> if (isLeader) sendCommand("party demote ${targetArgument(args[0], event.player)}", true)

                PartyCommandTypes.KICKOFFLINE -> if (isLeader) sendCommand("party kickoffline")

                PartyCommandTypes.DOWNTIME -> InstanceRequeue.requestDowntime(event.player, argsLiteral)

                PartyCommandTypes.QUEUE -> {
                    if (isLeader) {
                        val instance = queueMap[commandLiteral.lowercase()]
                        displayModMessage("Joining instance: $instance")
                        if (instance != null) sendCommand("joininstance $instance")
                    }
                }

                PartyCommandTypes.COORDS -> sendMessage("x: ${MC.player.blockX}, y: ${MC.player.blockY}, z: ${MC.player.blockZ}")

                PartyCommandTypes.FPS -> sendMessage("FPS: ${MC.fps}")

                PartyCommandTypes.TPS -> sendMessage("TPS: ${round(ServerUtils.tps * 100) / 100}")

                PartyCommandTypes.PING -> sendMessage("Ping: ${ServerUtils.currentPing}ms")

                PartyCommandTypes.DUNGEONS -> {
                    val floor = DungeonFloor.getByName(args[0].uppercase())
                    val data = DungeonTracker.profitData[floor]
                    if (floor != null && data != null) {
                        when (args[1].uppercase()) {
                            "XP" -> {
                                val xp = NumberUtils.condense(data.totalXp)
                                when (args[2].uppercase()) {
                                    "avg" -> sendModMessage("Avg Cata XP: $xp | ${data.classXp.map { xp -> "${xp.key.displayName}: ${xp.value / data.totalTimeHours}" }.joinToString(" | ")}", ChatChannel.PARTY)
                                    else -> sendModMessage("Total Cata XP: $xp | ${data.classXp.map { xp -> "${xp.key.displayName}: ${xp.value}" }.joinToString(" | ")}", ChatChannel.PARTY)
                                }
                            }
                            else -> {
                                val time = data.totalTimeMillis.milliseconds.inWholeSeconds.seconds
                                val runs = data.totalRuns
                                val chests = data.totalChestsOpened
                                val profit = NumberUtils.condense(data.netProfit)
                                val rate = NumberUtils.condense(data.netProfit / data.totalTimeHours)
                                sendModMessage("Time: $time | Runs: $runs | Chests: $chests | Profit: $profit ($rate/h)", ChatChannel.PARTY)
                            }
                        }
                    }
                }
            }
        }
    }

    fun targetArgument(arg: String, default: String) = if (arg.isBlank()) default else findPartyMember(arg)

    fun findPartyMember(name: String) = PartyAPI.members.firstOrNull { it.name?.contains(name, true) ?: false }?.name ?: name
}

enum class PartyCommandTypes(val aliases: Set<String>) {
    INVITE(setOf("invite", "inv", "i")),
    KICK(setOf("kick", "k")),
    ALLINVITE(setOf("allinvite", "allinv")),
    DISBAND(setOf("disband")),
    WARP(setOf("warp", "w")),
    TRANSFER(setOf("transfer", "pt", "ptme")),
    PROMOTE(setOf("promote")),
    DEMOTE(setOf("demote")),
    KICKOFFLINE(setOf("kickoffline", "ko")),
    QUEUE(PartyCommands.queueMap.keys.toSet()),
    DOWNTIME(setOf("downtime", "dt")),
    COORDS(setOf("coordinates", "coordinate", "cordinates", "cordinate", "coord", "coords", "cord", "cords")),
    FPS(setOf("fps")),
    TPS(setOf("tps")),
    PING(setOf("ping")),
    DUNGEONS(setOf("dungeon", "dungeons", "d"));

    companion object {
        fun fromAlias(alias: String): PartyCommandTypes? = entries.firstOrNull { alias.lowercase() in it.aliases }
    }
}