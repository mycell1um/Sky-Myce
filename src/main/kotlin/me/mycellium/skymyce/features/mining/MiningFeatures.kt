package me.mycellium.skymyce.features.mining

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.ChatChannel
import me.mycellium.skymyce.config.mining.MiningConfig
import me.mycellium.skymyce.hud.HudElement
import me.mycellium.skymyce.hud.elements.*
import me.mycellium.skymyce.hud.widget.Anchor
import me.mycellium.skymyce.hud.widget.Widget
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.PlayerUtils.sendCommand
import me.mycellium.skymyce.utils.PlayerUtils.sendMessage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import tech.thatgravyboat.skyblockapi.utils.Scheduling.schedule
import kotlin.math.floor
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object MiningFeatures : SkyMyceModule() {
    val crystalRegex = Regex("^\\s*✦ CRYSTAL FOUND \\((\\d+)/(\\d+)\\)$")

    var totalTime = 0L
    var timeSince = System.currentTimeMillis()

    const val CH_LOCK_DAY = 20.5
    const val CH_CLOSE_DAY = 35

    val crystalHollowsWidget = Widget("crystal_hollows_info", "CH Info", 200, 200, 1f, Anchor.TOP_LEFT) {
        if (!MiningConfig.crystalHollowsInfo) return@Widget null
        if (LocationAPI.island != SkyBlockIsland.CRYSTAL_HOLLOWS) return@Widget null

        val time = (MC.instance.level?.gameTime ?: 0L) - 60240000 // 2510 days in ticks
        val day = ((time / 20.0) / 60.0) / 20.0 // 20 ticks, 60 seconds, 20 minutes
        val lockTimeRemaining = (((CH_LOCK_DAY * 24000) - time) / 20).seconds
        val closeTimeRemaining = (((CH_CLOSE_DAY * 24000) - time) / 20).seconds

        val dayFormatted = when {
            (day > CH_CLOSE_DAY) -> "§c${floor(day * 100) / 100} (CLOSING)"
            (day > CH_LOCK_DAY) -> "§e${floor(day * 100) / 100} (LOCKED)"
            else -> "§a${floor(day * 100) / 100}"
        }

        val lines: MutableList<HudElement> = mutableListOf()
        lines += TextElement("§7Day: §e$dayFormatted")
        lines += TextElement("§7Player count: (§e${LocationAPI.playerCount}§7/§a${LocationAPI.maxPlayercount}§7)")
        lines += TextElement("§7Lobby Time: §e${(System.currentTimeMillis() - timeSince).milliseconds.inWholeSeconds.seconds}")
        lines += TextElement("§7Session Time: §e${(totalTime + System.currentTimeMillis() - timeSince).milliseconds.inWholeSeconds.seconds}")
        if (lockTimeRemaining > 0.seconds) {
            lines += TextElement("§7Time to lock: §e${lockTimeRemaining.inWholeSeconds.seconds}")
        } else if (closeTimeRemaining > 0.seconds && LocationAPI.playerCount > 3) {
            lines += TextElement("§7Time to close: §e${closeTimeRemaining.inWholeSeconds.seconds}")
        } else {
            lines += TextElement("§cLobby Closing!")
        }

        return@Widget PanelElement(
            StackElement(LayoutAxis.VERTICAL, LayoutType.START, children = lines),
            accent = null,
            border = null
        )
    }

    @Subscription
    fun onIslandChange(event: IslandChangeEvent) {
        if (event.old == SkyBlockIsland.CRYSTAL_HOLLOWS) {
            totalTime += System.currentTimeMillis() - timeSince
        }
        if (event.new == SkyBlockIsland.CRYSTAL_HOLLOWS) {
            timeSince = System.currentTimeMillis()
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (MiningConfig.autoNucleusWarp && crystalRegex.matches(event.text)) {
            schedule(Random.nextDouble(MiningConfig.autoNucleusWarpMin, MiningConfig.autoNucleusWarpMax).seconds) {
                sendCommand("warp nuc", true)
            }
        }

        if (MiningConfig.balNotify && PartyAPI.inParty) {
            when (event.text) {
                "The ground starts to shake and the lava bubbles..." -> sendMessage("Bal is spawning!", ChatChannel.PARTY)

                "The boss's outer shell looks to be weakening!" -> sendMessage("Bal 75% HP", ChatChannel.PARTY)

                "Half way there! The boss is starting to become weaker!" -> sendMessage("Bal 50% HP", ChatChannel.PARTY)

                "Nearly there! The boss is shaking, it can't last much longer!" -> sendMessage("Bal 25% HP", ChatChannel.PARTY)

                "The boss looks weak and tired and retreats into the lava..." -> sendMessage("Bal dead!", ChatChannel.PARTY)
            }
        }
    }
}