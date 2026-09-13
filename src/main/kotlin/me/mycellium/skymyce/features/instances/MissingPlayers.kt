package me.mycellium.skymyce.features.instances

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.instances.InstancesConfig
import me.mycellium.skymyce.utils.Utils.displayTitle
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI

object MissingPlayers : SkyMyceModule() {
    val startingRegex = Regex("^Starting in \\d+ seconds.%")

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (!InstancesConfig.missingPlayers) return
        if (startingRegex.matches(event.text)) {
            if (LocationAPI.playerCount < PartyAPI.members.size) {
                displayTitle("§c§lMISSING PLAYERS", "§e${LocationAPI.playerCount}/${PartyAPI.members.size}")
            }
        }
    }
}