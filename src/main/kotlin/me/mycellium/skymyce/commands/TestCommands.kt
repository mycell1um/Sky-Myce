package me.mycellium.skymyce.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket

object TestCommands {
    fun command(): LiteralArgumentBuilder<FabricClientCommandSource> {
        return literal("test")
            .then(packetCommand())
    }

    private fun packetCommand(): LiteralArgumentBuilder<FabricClientCommandSource> {
        return literal("packet")
            .then(
                literal("party")
                    .executes {
                        HypixelModAPI.getInstance().sendPacket(ServerboundPartyInfoPacket())
                        1
                    }
            )
            .then(
                literal("ping")
                    .executes {
                        HypixelModAPI.getInstance().sendPacket(ServerboundPingPacket())
                        1
                    }
            )
    }

    private fun formatGroups(groups: List<String>?): String {
        return buildString {
            repeat(10) { index ->
                append("[$index] ${groups?.getOrNull(index)}\n")
            }
        }
    }
}
