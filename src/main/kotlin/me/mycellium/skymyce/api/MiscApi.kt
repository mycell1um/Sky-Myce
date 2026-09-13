package me.mycellium.skymyce.api

import kotlinx.coroutines.delay
import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.api.events.PlayerMessageEvent
import me.mycellium.skymyce.api.events.ChatChannel
import me.mycellium.skymyce.api.events.ServerTickEvent
import net.minecraft.network.protocol.game.ClientboundSetTimePacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketEvent
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull

object MiscApi : SkyMyceModule() {
    const val RANK_USERNAME = "(?:\\[.+?] )?(?<player>\\w{1,16})"

    private val chatGroup = RegexGroup.CHAT.group("message")
    private val publicGroup = chatGroup.create("public", "^\\[\\d+](?: .)? $RANK_USERNAME: (?<message>.+)$")
    private val directGroup = chatGroup.create("direct", "^From $RANK_USERNAME: (?<message>.+)$")
    private val partyGroup = chatGroup.create("party", "^Party > $RANK_USERNAME: (?<message>.+)$")
    private val guildGroup = chatGroup.create("guild", "^Guild > $RANK_USERNAME(?: \\[([^]]+)])?: (?<message>.+)$")

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        publicGroup.findThenNull(event.text, "player", "message") { (player, message) ->
            PlayerMessageEvent(ChatChannel.PUBLIC, player, message).post(SkyBlockAPI.eventBus)
        } ?: return

        directGroup.findThenNull(event.text, "player", "message") { (player, message) ->
            PlayerMessageEvent(ChatChannel.PRIVATE, player, message).post(SkyBlockAPI.eventBus)
        } ?: return

        partyGroup.findThenNull(event.text, "player", "message") { (player, message) ->
            PlayerMessageEvent(ChatChannel.PARTY, player, message).post(SkyBlockAPI.eventBus)
        } ?: return

        guildGroup.findThenNull(event.text, "player", "message") { (player, message) ->
            PlayerMessageEvent(ChatChannel.GUILD, player, message).post(SkyBlockAPI.eventBus)
        } ?: return
    }

    private var lastClientTime = -1L
    private var lastServerTime = -1L

    @Subscription
    suspend fun onPacket(event: PacketEvent) {
        val packet = event.packet as? ClientboundSetTimePacket ?: return

        val clientTime = System.currentTimeMillis()
        val serverTime = packet.gameTime

        if (lastServerTime == -1L) {
            lastClientTime = clientTime
            lastServerTime = serverTime
            return
        }

        val timePassed = serverTime - lastServerTime
        val ticksPassed = serverTime - lastServerTime

        for (tick in lastServerTime until serverTime) {
            ServerTickEvent(tick).post(SkyBlockAPI.eventBus)
            delay(timePassed / ticksPassed)
        }

        lastClientTime = clientTime
        lastServerTime = serverTime
    }

}