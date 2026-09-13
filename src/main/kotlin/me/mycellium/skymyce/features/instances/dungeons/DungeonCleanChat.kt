package me.mycellium.skymyce.features.instances.dungeons

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.config.instances.dungeons.DungeonsConfig
import me.mycellium.skymyce.features.instances.dungeons.DungeonCleanChat.DungeonFilter.Companion.filter
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object DungeonCleanChat : SkyMyceModule() {
    enum class DungeonFilter(vararg val regexes: Regex) {
        MISCELLANEOUS(
            Regex("^Potion Effect! (.+)$"),
            Regex("^(.+) has started the dungeon countdown. The dungeon will begin in 1 minute.$"),
            Regex("^Your active Potion Effects have been paused and stored. They will be restored when you leave Dungeons! You are not allowed to use existing Potion Effects while in Dungeons.$"),
            Regex("^You can no longer consume or splash any potions during the remainder of this Dungeon run!$"),
            Regex("^(\\w+) the Fairy: (.+)$"),
            Regex("^A mystical force prevents you digging in this room!$"),
            Regex("^A shiver runs down your spine...$"),
            Regex("^You cannot hit the silverfish while it's moving!$"),
            Regex("^You cannot move the silverfish in that direction!$"),
            Regex("^This chest has already been searched!$"),
            Regex("^This lever has already been used.$"),
            Regex("^You cannot do that in this room!$"),
            Regex("^You have already opened this dungeon chest!$"),
            Regex("^You cannot use abilities in this room!$"),
            Regex("^You don't have enough charges to break this block right now!$"),
            Regex("^A mystical force in this room prevents you from using that ability!$"),
            Regex("^A mystical force prevents you from digging that block!$"),
            Regex("^A mystical force prevents you digging there!$"),
            Regex("^◕ (.+) picked up your (.+) Orb!$"),
        ),
        DISCOVERIES(
            Regex("^DUNGEON BUFF! (.+) found a Blessing of (.+)!( \\([\\dhms ]+\\))?$"),
            Regex("^DUNGEON BUFF! A Blessing of (.+) was found!( \\([\\dhms ]+\\))?$"),
            Regex("^A Blessing of (.+) was picked up!$"),
            Regex("^\\s*(Granted|Also granted) you (.+).$"),
            Regex("^ESSENCE! (.+) found x(\\d+) (.+) Essence!$"),
            Regex("^(.+) found a (.+) Essence! Everyone gains an extra essence!$"),
        ),
        OBTAINED(
            Regex("^(.+) has obtained (.+)!$")
        ),
        ABILITIES(
            Regex("^Used (.+?)!$"),
            Regex("^(.+) is now available!$"),
            Regex("^(.+) is ready to use! Press DROP to activate it!$")
        ),
        PUZZLES(
            Regex("^PUZZLE SOLVED! (.+)$"),
            Regex("^[STATUE] Oruo the Omniscient: (.+)$"),
            Regex("^Question #(\\d+)$"),
            Regex("^\\s*(ⓐ|ⓑ|ⓒ) (.+)$"),
        ),
        DOOR(
            Regex("^RIGHT CLICK on (a WITHER door|the BLOOD DOOR) to open it. This key can only be used to open 1 door!$"),
            Regex("^(.+) opened a WITHER door!$"),
            Regex("^A (Blood|Wither) Key was picked up!$"),
            Regex("^The BLOOD DOOR has been opened!$"),
            Regex("^You do not have the key for this door!$"),
        ),
        DIALOG(
            Regex("^\\[[A-Z]+] (.+?): (.+)$"),
        );

        companion object {
            fun DungeonFilter.filter(message: String): Boolean {
                return this.regexes.any { regex ->
                    regex.matches(message)
                }
            }
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onServerChatEvent(event: ChatReceivedEvent.Pre) {
        if (DungeonsConfig.dungeonMessageFilter.any { it.filter(event.text) }) {
            event.cancel()
        }
    }
}