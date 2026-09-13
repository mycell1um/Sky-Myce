package me.mycellium.skymyce.hud.widget

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement
import me.mycellium.skymyce.hud.widget.WidgetManager.register

class Widget(
    val id: String,
    val title: String,
    var x: Int = 0,
    var y: Int = 0,
    var scale: Float = 1f,
    var anchor: Anchor = Anchor.TOP_LEFT,
    val builder: Widget.() -> HudElement? = { null }
) {
    var sinceActive: Long = 0L

    var lastRenderable: HudElement? = null
        private set

    var isRenderable: Boolean = false

    val renderX: Int
        get() = x - (anchor.x * width).toInt()

    val renderY: Int
        get() = y - (anchor.y * height).toInt()

    val width: Int
        get() = ((lastRenderable?.width ?: 0) * scale).toInt()

    val height: Int
        get() = ((lastRenderable?.height ?: 0) * scale).toInt()

    init {
        this.register()
    }

    fun getClosestAnchor(x: Int, y: Int): Anchor {
        return Anchor.entries.minByOrNull { anchor ->
            val dx = x - (anchor.x * width) - renderX
            val dy = y - (anchor.y * height) - renderY
            return@minByOrNull dx * dx + dy * dy
        } ?: Anchor.entries[0]
    }

    fun setAnchorPreservePosition(newAnchor: Anchor) {
        x = renderX + (newAnchor.x * width).toInt()
        y = renderY + (newAnchor.y * height).toInt()
        anchor = newAnchor
    }

    fun toConfig(): WidgetConfig = WidgetConfig(x, y, scale, anchor)

    fun inBounds(selX: Int, selY: Int): Boolean = selX in renderX..renderX + width && selY in renderY..renderY + height

    fun render(ctx: HudContext) {
        val element = builder()
        isRenderable = (element != null)

        if (!isRenderable) return
        lastRenderable = element
        sinceActive = System.currentTimeMillis()

        ctx.graphics.pose().pushMatrix()
        ctx.graphics.pose().translate(renderX.toFloat(), renderY.toFloat())
        ctx.graphics.pose().scale(scale)

        lastRenderable?.renderAll(ctx, 0, 0)

        ctx.graphics.pose().popMatrix()
    }
}