package me.mycellium.skymyce.utils

import me.mycellium.skymyce.api.events.ChatChannel
import me.mycellium.skymyce.utils.Utils.displayModMessage
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import java.util.*

object PlayerUtils {
    val uuid: UUID get() = MC.player.uuid
    val activeItem: ItemStack get() = MC.player.activeItem
    val heldItem: ItemStack get() = MC.player.mainHandItem
    val helmet: ItemStack get() = MC.player.getItemBySlot(EquipmentSlot.HEAD)
    val chestplate: ItemStack get() = MC.player.getItemBySlot(EquipmentSlot.CHEST)
    val leggings: ItemStack get() = MC.player.getItemBySlot(EquipmentSlot.LEGS)
    val boots: ItemStack get() = MC.player.getItemBySlot(EquipmentSlot.FEET)

    fun sendMessage(message: String, channel: ChatChannel? = null) {
        when (channel) {
            ChatChannel.PUBLIC -> sendCommand("ac $message")
            ChatChannel.PRIVATE -> sendCommand("r $message")
            ChatChannel.PARTY -> sendCommand("pc $message")
            ChatChannel.GUILD -> sendCommand("gc $message")
            else -> MC.connection?.sendChat(message)
        }
    }

    fun sendModMessage(text: String, channel: ChatChannel? = null) {
        sendMessage("[SkyMyce] $text", channel)
    }

    fun sendCommand(command: String, sendFeedback: Boolean = false) {
        if (sendFeedback) displayModMessage("§7Sending command: §b$command")
        MC.connection?.sendCommand(command)
    }
}