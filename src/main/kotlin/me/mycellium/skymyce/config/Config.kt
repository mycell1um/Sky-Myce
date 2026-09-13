package me.mycellium.skymyce.config

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.mycellium.skymyce.config.instances.InstancesConfig
import me.mycellium.skymyce.config.mining.MiningConfig
import me.mycellium.skymyce.config.misc.GeneralConfig
import org.lwjgl.glfw.GLFW

object Config : ConfigKt("SkyMyce Config") {
    override val version = 0

    init {
        category(GeneralConfig)
        category(InstancesConfig)
        category(MiningConfig)
    }

    var openConfigKey by key(GLFW.GLFW_KEY_RIGHT_SHIFT) {
        name = TranslatableValue.literal("Open Config Key")
        description = TranslatableValue.literal("Press this key to open the config screen")
    }

    var gatherData by boolean(false) {
        name = TranslatableValue.literal("Gather Data")
        description = TranslatableValue.literal("Enable to gather data to help future updates!")
    }

    var devMode by boolean(false) {
        name = TranslatableValue.literal("Dev Mode")
        description = TranslatableValue.literal("Enables developer mode")
    }
}
