package me.mycellium.skymyce.hud

abstract class HudElement {
    var x: Int = 0
    var y: Int = 0

    open val width: Int = 0
    open val height: Int = 0

    private var isHovered: Boolean = false

    fun isHovered(): Boolean = isHovered

    fun setHovered(bool: Boolean) { isHovered = bool }

    fun renderAll(context: HudContext, originX: Int? = null, originY: Int? = null) {
        x = originX ?: x
        y = originY ?: y
        layout()
        render(context)
    }

    open fun mouseClicked(context: HudContext, button: Int) {}

    open fun mouseScrolled(context: HudContext, amount: Double) {}

    open fun measure() {}

    open fun layout() {}

    open fun render(context: HudContext) {}
}
