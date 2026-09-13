package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement

class ButtonElement(
    val text: String,
    val color: Int = 0xFF555555.toInt(),
    val hovered: Int = 0xFF55FFFF.toInt(),
    val onClick: ButtonElement.() -> Unit = {}
) : HudElement() {
    override fun render(context: HudContext) {
        context.graphics.fill(x, y, x + width, y +height, if (isHovered()) hovered else color)
        context.graphics.centeredText(context.font, text, x + width / 2, y + height / 2, 0xFFFFFFFF.toInt())
    }

    override fun mouseClicked(context: HudContext, button: Int) {
        if (isHovered()) onClick()
        super.mouseClicked(context, button)
    }
}
