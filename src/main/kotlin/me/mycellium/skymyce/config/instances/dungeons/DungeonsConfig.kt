package me.mycellium.skymyce.config.instances.dungeons

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.mycellium.skymyce.features.instances.dungeons.DungeonCleanChat
import org.lwjgl.glfw.GLFW

object DungeonsConfig : CategoryKt("Dungeons") {
    init {
        category(DungeonTrackerConfig)
    }

    val dungeonWinScreen by boolean(true) {
        name = TranslatableValue.literal("Dungeon Win Screen")
        description = TranslatableValue.literal("Displays an overlay when a dungeon is ended")
    }

    val dungeonMessageFilter by select(DungeonCleanChat.DungeonFilter.MISCELLANEOUS) {
        name = TranslatableValue.literal("Dungeon Message Filter")
        description = TranslatableValue.literal("Filters dungeon chat messages by category")
    }

    val safeReroll by boolean(true) {
        name = TranslatableValue.literal("Safe Reroll")
        description = TranslatableValue.literal("Blocks a chest reroll if it contains a valuable item")
    }

    val safeRerollKey by key(GLFW.GLFW_KEY_LEFT_CONTROL) {
        name = TranslatableValue.literal("Key")
        description = TranslatableValue.literal("Hold this key to override rerolling a chest with valuable items")
    }

    val dungeonChestProfit by boolean(true) {
        name = TranslatableValue.literal("Dungeon Chest Profit")
        description = TranslatableValue.literal("Shows the profit and value of all items while in a dungeon chest")
    }

    enum class BazaarType {
        INSTANT_BUY,
        INSTANT_SELL
    }

    enum class AuctionType {
        LOWEST,
        HIGHEST,
        MEAN,
        MEDIAN
    }
}