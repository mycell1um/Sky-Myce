package me.mycellium.skymyce.config.misc

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import org.lwjgl.glfw.GLFW

object WardrobeConfig : CategoryKt("Wardrobe Keybinds") {
    val enabled by boolean(false) {
        name = TranslatableValue.literal("Enabled")
    }

    val preventUnequip by boolean(true) {
        name = TranslatableValue.literal("Prevent Unequip")
        description = TranslatableValue.literal("Prevents unequipping your current armor set")
    }

    val autoClose by boolean(true) {
        name = TranslatableValue.literal("Auto Close")
        description = TranslatableValue.literal("Automatically close the wardrobe menu after swapping")
    }

    val slot1 by key(GLFW.GLFW_KEY_4) {
        name = TranslatableValue("Slot 1")
    }
    val slot2 by key(GLFW.GLFW_KEY_5) {
        name = TranslatableValue("Slot 2")
    }
    val slot3 by key(GLFW.GLFW_KEY_6) {
        name = TranslatableValue("Slot 3")
    }
    val slot4 by key(GLFW.GLFW_KEY_R) {
        name = TranslatableValue("Slot 4")
    }
    val slot5 by key(GLFW.GLFW_KEY_T) {
        name = TranslatableValue("Slot 5")
    }
    val slot6 by key(GLFW.GLFW_KEY_Y) {
        name = TranslatableValue("Slot 6")
    }
    val slot7 by key(GLFW.GLFW_KEY_F) {
        name = TranslatableValue("Slot 7")
    }
    val slot8 by key(GLFW.GLFW_KEY_G) {
        name = TranslatableValue("Slot 8")
    }
    val slot9 by key(GLFW.GLFW_KEY_H) {
        name = TranslatableValue("Slot 9")
    }
    val slot10 by key(GLFW.GLFW_KEY_V) {
        name = TranslatableValue("Slot 10")
    }
    val slot11 by key(GLFW.GLFW_KEY_B) {
        name = TranslatableValue("Slot 11")
    }
    val slot12 by key(GLFW.GLFW_KEY_N) {
        name = TranslatableValue("Slot 12")
    }
}