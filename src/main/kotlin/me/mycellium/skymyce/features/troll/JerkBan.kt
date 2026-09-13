package me.mycellium.skymyce.features.troll

import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.Utils
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent

object JerkBan : SkyMyceModule() {
    @Subscription
    @TimePassed("1s")
    fun onPlayer(event: TickEvent) {
        val jerk = MC.instance.level?.players()?.filter { it != MC.player }?.map { it.getItemBySlot(EquipmentSlot.HEAD).getData(DataTypes.ID) }
        if (jerk != null && jerk.any { it == "SKULL_ITEM:3" }) {
            MC.connection?.connection?.disconnect(Component.literal("§cYou are temporarily banned for §f29d 23h 59m 59s§c from this server!\n§7Reason: §fSuspicious activity has been detected on your account\n§fFind out more: §b§nhttps://www.hypixel.net/appeal\n§7Ban ID: §f#5D52A3C7"))
        }
    }
}