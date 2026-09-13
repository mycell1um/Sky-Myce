package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement
import net.minecraft.world.item.ItemStack

class ItemElement(
    val item: ItemStack,
    val scale: Float = 1f,
) : HudElement() {
    override val width: Int
        get() = (scale * 16).toInt()

    override val height: Int
        get() = (scale * 16).toInt()

    override fun render(context: HudContext) {
        context.graphics.pose().pushMatrix()

        context.graphics.pose().translate(x.toFloat(), y.toFloat())
        context.graphics.pose().scale(scale)

        context.graphics.item(item, 0, 0)

        context.graphics.pose().popMatrix()
    }

    override fun mouseClicked(context: HudContext, button: Int) {
    }
}
