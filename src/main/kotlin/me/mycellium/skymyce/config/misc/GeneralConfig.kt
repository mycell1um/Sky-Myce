package me.mycellium.skymyce.config.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.mycellium.skymyce.features.general.AutoRefill

object GeneralConfig : CategoryKt("General") {
    init {
        category(PartyCommandsConfig)
        category(StashHelperConfig)
        category(WardrobeConfig)
        category(LoadoutConfig)
    }

    val clientInfo by boolean(false) {
        name = TranslatableValue.literal("Display Client Info")
        description = TranslatableValue.literal("Shows client info on screen")
    }

    val autoRefill by select<AutoRefill.Types> {
        name = TranslatableValue.literal("Auto Refill")
        description = TranslatableValue.literal("Automatically refill these items when it runs out")
    }

    val displayXp by boolean(true) {
        name = TranslatableValue.literal("Display Skill XP")
        description = TranslatableValue.literal("Display Skill XP on your screen")
    }

    val displayAbilityCooldown by boolean(true) {
        name = TranslatableValue.literal("Display Ability Cooldown")
        description = TranslatableValue.literal("Displays ability cooldown timers when you hold an item")
    }

    val displayAbilityNotification by boolean(false) {
        name = TranslatableValue.literal("Display Ability Notification")
        description = TranslatableValue.literal("Displays a notification when an ability's cooldown resets")
    }
}