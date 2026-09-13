package me.mycellium.skymyce.config.instances

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.mycellium.skymyce.config.instances.dungeons.DungeonsConfig

object InstancesConfig : CategoryKt("Instances") {
    init {
        category(DungeonsConfig)
    }

    val missingPlayers by boolean(true) {
        name = TranslatableValue.literal("Notify Missing Players")
        description = TranslatableValue.literal("Displays if you are missing players when joining an instance")
    }

    val autoRequeue by boolean(true) {
        name = TranslatableValue.literal("Auto Requeue")
        description = TranslatableValue.literal("Automatically requeues your instance when it ends\n§c(Enable \"downtime\" in Party Commands for downtime features to work!)")
    }

    val requeueDelay by double(1.0) {
        name = TranslatableValue.literal("Requeue Delay")
        description = TranslatableValue.literal("The delay in seconds before requeuing")

        slider = true
        range = 0.0..5.0
    }
}