package me.mycellium.skymyce.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.mycellium.skymyce.api.AuctionApi
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonTracker
import me.mycellium.skymyce.utils.PlayerUtils
import me.mycellium.skymyce.utils.Utils.displayMessage
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonFloor
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object DevCommands {

    fun command(): LiteralArgumentBuilder<FabricClientCommandSource> {
        return literal("dev")
            .then(
                literal("auction_data")
                    .executes {
                        AuctionApi.getData()
                        1
                    }
            )
            .then(
                literal("dungeon")
                    .then(
                        argument("floor", StringArgumentType.word())
                            .executes { context ->
                                val floor = StringArgumentType.getString(context, "floor")
                                DungeonTracker.currentFloor = DungeonFloor.valueOf(floor)
                                1
                            }
                    )
            )
            .then(
                literal("name")
                    .executes {
                        displayMessage("name: ${SkyBlockId.fromName(PlayerUtils.heldItem.hoverName.stripped, true)}")
                        1
                    }
                    .then(
                        argument("name", StringArgumentType.string())
                            .executes { context ->
                                val name = StringArgumentType.getString(context, "name")
                                displayMessage("name: ${SkyBlockId.fromName(name, true)}")
                                1
                            }
                    )
            )
    }
}
