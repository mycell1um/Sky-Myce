package me.mycellium.skymyce.hud

import me.mycellium.skymyce.utils.MC
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor

data class HudContext(
    val graphics: GuiGraphicsExtractor,
    val mouseX: Float = 0f,
    val mouseY: Float = 0f,
    var tooltip: HudElement? = null,
) {
    private val REFERENCE_WIDTH = 960f
    private val REFERENCE_HEIGHT = 540f

    val guiWidth = MC.instance.window.guiScaledWidth.toFloat()
    val guiHeight = MC.instance.window.guiScaledHeight.toFloat()

    val scale: Float
        get() = guiHeight / REFERENCE_HEIGHT

    val width: Float
        get() = guiWidth / scale

    val height: Float
        get() = REFERENCE_HEIGHT

    fun x(x: Number) = (x.toFloat() / REFERENCE_WIDTH) * guiWidth
    fun y(y: Number) = (y.toFloat() / REFERENCE_WIDTH) * guiWidth

    fun mouseX() = ((MC.instance.mouseHandler.xpos() / MC.instance.window.screenWidth) * width).toInt()
    fun mouseY() = ((MC.instance.mouseHandler.ypos() / MC.instance.window.screenHeight) * height).toInt()

    val font: Font
        get() = MC.font

}