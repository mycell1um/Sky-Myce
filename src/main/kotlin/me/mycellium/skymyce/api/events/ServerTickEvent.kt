package me.mycellium.skymyce.api.events

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class ServerTickEvent(val tick: Long) : SkyBlockEvent()