package me.mycellium.skymyce.config.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object StashHelperConfig : CategoryKt("Stash Helper") {
    init {
        separator {
            title = "Stash Helper"
            description = "Hides useless items when in your stash"
        }
    }

    val stashHelper by boolean(true) {
        name = TranslatableValue.literal("Enabled")
    }

    val stashValueThreshold by double(0.1) {
        name = TranslatableValue.literal("Threshold")
        description = TranslatableValue.literal("Any item worth less than this value in millions will be hidden")

        slider = true
        range = 0.0..10.0
    }

    val blockUseless by boolean(true) {
        name = TranslatableValue.literal("Block Useless Clicks")
        description = TranslatableValue.literal("Blocks picking up useless items from your stash")
    }
}