package me.mycellium.skymyce

import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import me.mycellium.skymyce.api.AuctionApi
import me.mycellium.skymyce.api.DungeonApi
import me.mycellium.skymyce.api.MiscApi
import me.mycellium.skymyce.commands.SkyMyceCommands
import me.mycellium.skymyce.config.Config
import me.mycellium.skymyce.features.dev.DevTooltips
import me.mycellium.skymyce.features.general.*
import me.mycellium.skymyce.features.general.overlays.AbilityCooldownOverlay
import me.mycellium.skymyce.features.general.overlays.ClientInfoOverlay
import me.mycellium.skymyce.features.general.overlays.SkillXpOverlay
import me.mycellium.skymyce.features.instances.InstanceRequeue
import me.mycellium.skymyce.features.instances.MissingPlayers
import me.mycellium.skymyce.features.instances.dungeons.DungeonChestOverlay
import me.mycellium.skymyce.features.instances.dungeons.DungeonCleanChat
import me.mycellium.skymyce.features.instances.dungeons.DungeonWinOverlay
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonTracker
import me.mycellium.skymyce.features.instances.dungeons.tracker.DungeonTrackerWidget
import me.mycellium.skymyce.features.mining.MiningFeatures
import me.mycellium.skymyce.features.troll.JerkBan
import me.mycellium.skymyce.hud.HudRenderer
import me.mycellium.skymyce.hud.widget.WidgetManager
import me.mycellium.skymyce.utils.MC
import me.mycellium.skymyce.utils.ServerUtils
import me.mycellium.skymyce.utils.Utils
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import java.nio.file.Path


object SkyMyce : ClientModInitializer {
    val mod = FabricLoader.getInstance().getModContainer("skymyce").orElseThrow()
    val MOD_ID = mod.metadata.id
    val version = mod.metadata.version.friendlyString

    val logger = LoggerFactory.getLogger(MOD_ID)

    val configPath: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)

    val configurator = Configurator(MOD_ID)
    lateinit var config: ResourcefulConfig

    override fun onInitializeClient() {
        config = Config.register(configurator)

        ModuleManager.loadModules(
            JerkBan,
            MiscApi, AuctionApi, DungeonApi, ServerUtils, HudRenderer, WidgetManager,
            DevTooltips,
            LoadoutKeybinds, SkyMyceCommands, SkillXpOverlay, AbilityCooldownOverlay, AutoRefill, ClientInfoOverlay, PartyCommands, Waypoints,
            StashHelper, InstanceRequeue, MissingPlayers, DungeonChestOverlay, DungeonTracker, DungeonTrackerWidget, DungeonCleanChat, DungeonWinOverlay,
            MiningFeatures
        )

        ModuleManager.modules.forEach {
            SkyBlockAPI.eventBus.register(it)
            it.init()
        }
    }

    fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)
}
