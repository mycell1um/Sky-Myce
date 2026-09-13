package me.mycellium.skymyce.hud

import me.mycellium.skymyce.SkyMyce
import me.mycellium.skymyce.SkyMyceModule
import me.mycellium.skymyce.hud.widget.WidgetManager
import me.mycellium.skymyce.utils.MC
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

object HudRenderer : SkyMyceModule() {
    override fun init() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.fromNamespaceAndPath(SkyMyce.MOD_ID, "hud_renderer")
        ) { context, _ -> render(context) }

//        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
//            ScreenMouseEvents.afterMouseClick(screen).register { _, mouse, consumed ->
//            }
//            ScreenMouseEvents.afterMouseScroll(screen).register { _, mouseX, mouseY, _, verticalAmount, consumed ->
//            }
//        }
    }

    fun render(context: GuiGraphicsExtractor) {
        if (MC.instance.options.hideGui) return
        val hudContext = HudContext(context, scaledMouseX(MC.instance), scaledMouseY(MC.instance))

        WidgetManager.widgets.forEach {
            it.render(hudContext)
        }

        hudContext.tooltip?.renderAll(hudContext, hudContext.mouseX.toInt(), hudContext.mouseY.toInt())
    }

    fun scaledMouseX(client: Minecraft): Float = (client.mouseHandler.xpos() * client.window.guiScaledWidth / client.window.screenWidth).toFloat()

    fun scaledMouseY(client: Minecraft): Float = (client.mouseHandler.ypos() * client.window.guiScaledHeight / client.window.screenHeight).toFloat()
}