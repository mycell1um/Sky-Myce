package me.mycellium.skymyce.api.events

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class PlayerMessageEvent(val type: ChatChannel, val player: String, val message: String) : SkyBlockEvent()

enum class ChatChannel {
    PUBLIC,
    PRIVATE,
    PARTY,
    GUILD,
}