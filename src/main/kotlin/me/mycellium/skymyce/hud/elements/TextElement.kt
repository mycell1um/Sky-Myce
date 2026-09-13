package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement
import me.mycellium.skymyce.utils.MC

class TextElement(
    val text: String,
    val tooltip: HudElement? = null,
) : HudElement() {
    override val width: Int
        get() = MC.font.width(text)

    override val height: Int
        get() = MC.font.lineHeight

    override fun render(context: HudContext) {
        context.graphics.text(context.font, text, x, y, 0xFFFFFFFF.toInt())

        if (isHovered() && tooltip != null) {
            context.tooltip = tooltip
        }
    }
}
