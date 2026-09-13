package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement

class FixedPanelElement(
    val child: HudElement,
    val contentWidth: Int,
    val contentHeight: Int,
    val padding: Int = 5,
    val background: Int? = 0x88000000.toInt(),
    val border: Int? = 0x5555FFFF.toInt(),
    val accent: Int? = 0xFF55FFFF.toInt(),
) : HudElement() {
    override val width: Int = contentWidth + padding * 2
    override val height: Int = contentHeight + padding * 2

    override fun layout() {
        child.x = x + padding
        child.y = y + padding
        child.layout()
    }

    override fun render(context: HudContext) {
        if (background != null) context.graphics.fill(x, y, x + width, y + height, background)
        if (border != null) context.graphics.outline(x, y, width, height, border)
        if (accent != null) context.graphics.fill(x, y, x + 2, y + height, accent)

        context.graphics.enableScissor(x + padding, y + padding, x + width - padding, y + height - padding)
        child.render(context)
        context.graphics.disableScissor()
    }
}
