package me.mycellium.skymyce.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import me.mycellium.skymyce.ModuleManager
import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.features.general.AuctionHouseScreen
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonScreen
import me.mycellium.skymyce.hud.widget.WidgetEditorScreen
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.Utils.displayMessage
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource


object SkyMyceCommands : SkyMyceModule() {
    override fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val root = dispatcher.register(root())
            dispatcher.register(
                literal("sm").redirect(root)
            )
        }
    }

    fun root(): LiteralArgumentBuilder<FabricClientCommandSource?>? {
        return literal("skymyce")
            .executes {
                MC.instance.schedule {
                    MC.instance.setScreen(
                        ResourcefulConfigScreen.make(SkyMyce.config).build()
                    )
                }
                1
            }
            .then(dungeon())
            .then(auction())
            .then(hud())
            .then(modules())
            .then(DevCommands.command())
            .then(TestCommands.command())
    }

    fun dungeon(): LiteralArgumentBuilder<FabricClientCommandSource?>? {
        return literal("dungeon")
            .executes {
                MC.instance.execute {
                    MC.instance.setScreen(DungeonScreen())
                }
                1
            }
    }

    fun auction(): LiteralArgumentBuilder<FabricClientCommandSource?>? {
        return literal("ah")
            .executes {
                MC.instance.execute {
                    MC.instance.setScreen(AuctionHouseScreen())
                }
                1
            }
    }

    fun hud(): LiteralArgumentBuilder<FabricClientCommandSource?>? {
        return literal("hud")
            .executes {
                MC.instance.execute {
                    MC.instance.setScreen(WidgetEditorScreen)
                }
                1
            }
    }

    fun modules(): LiteralArgumentBuilder<FabricClientCommandSource?>? {
        return literal("modules")
            .executes {
                displayMessage("§b§l--= Modules: ${ModuleManager.modules.size} =--")
                ModuleManager.modules.forEach  {
                    displayMessage("§${if (it.isEnabled()) "a✔" else "c❌"} ${it.javaClass.simpleName}")
                }
                1
            }
    }
}
