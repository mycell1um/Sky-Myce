package me.mycellium.skymyce.config.mining

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object MiningConfig : CategoryKt("Mining") {
    val powderTracker by boolean(false) {
        name = TranslatableValue.literal("Power Tracker")
        description = TranslatableValue.literal("Tracks powder gains while mining")
    }

    val crystalHollowsInfo by boolean(true) {
        name = TranslatableValue.literal("Crystal Hollows Info")
        description = TranslatableValue.literal("Displays useful Crystal Hollows info in your lobby")
    }

    val balNotify by boolean(false) {
        name = TranslatableValue.literal("Bal Notifier")
        description = TranslatableValue.literal("Notifies bal HP in chat §c(Only works while in a party)")
    }

    val autoNucleusWarp by boolean(false) {
        name = TranslatableValue.literal("Auto Nucleus Warp")
        description = TranslatableValue.literal("Automatically types \"/warp nuc\" when obtaining a crystal in the crystal hollows")
    }

    val autoNucleusWarpMin by double(0.3) {
        name = TranslatableValue.literal("Warp Min")

        slider = true
        range = 0.0..1.0
    }

    val autoNucleusWarpMax by double(0.7) {
        name = TranslatableValue.literal("Warp Max")

        slider = true
        range = 0.0..1.0
    }
}