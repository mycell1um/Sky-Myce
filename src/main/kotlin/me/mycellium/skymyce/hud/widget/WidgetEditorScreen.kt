package me.mycellium.skymyce.hud.widget

import me.mycellium.skymyce.utils.MC
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object WidgetEditorScreen : Screen(MC.instance, MC.font, Component.literal("Widget Editor")) {
    private var selectedWidget: Widget? = null
    private var clickedWidget: Widget? = null
    private var selectedAnchor: Anchor? = null
    private var screenWidgets: List<Widget> = emptyList()

    private var mouseRelX = 0
    private var mouseRelY = 0
    private var snapPosition = false
    private var anchorSelect = false

    override fun init() {
        screenWidgets = WidgetManager.getActiveWidgets()

        val resetButton = Button.builder(
            Component.literal("Reset HUD Positions")
        ) {
            screenWidgets.forEach { widget ->
                widget.x = width / 2
                widget.y = height / 2
                widget.scale = 1f
                widget.anchor = Anchor.CENTER
            }
        }.bounds(width / 2 - 75, height - 30, 150, 20).build()

        addRenderableWidget(resetButton)
        super.init()
    }

    override fun onClose() {
        WidgetManager.save()
        super.onClose()
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        graphics.fill(0, 0, width, height, 0x88000000.toInt())
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        screenWidgets.forEach { widget ->
            val backgroundColor = if (clickedWidget == widget) 0x88FFFF55.toInt() else if (selectedWidget == widget) 0x55FFFFFF.toInt() else 0x55000000.toInt()
            val outlineColor = if (clickedWidget == widget) 0xFFFFFF55.toInt() else 0xFF55FFFF.toInt()

            graphics.fill(widget.renderX, widget.renderY, widget.renderX + widget.width, widget.renderY + widget.height, backgroundColor)
            graphics.outline(widget.renderX, widget.renderY, widget.width, widget.height, outlineColor)
            graphics.centeredText(MC.font, widget.title, widget.renderX + (widget.width / 2), widget.renderY + ((widget.height - MC.font.lineHeight) / 2), outlineColor)
            graphics.centeredText(MC.font, "X: ${widget.x} Y: ${widget.y} Scale: ${widget.scale}", widget.renderX + (widget.width / 2), widget.renderY + (widget.height) + 10, outlineColor)
        }

        if (anchorSelect) {
            selectedWidget?.let { widget ->
                Anchor.entries.forEach { anchor ->
                    val x = widget.renderX + (anchor.x * widget.width).toInt()
                    val y = widget.renderY + (anchor.y * widget.height).toInt()
                    graphics.fill(x - 2, y - 2, x + 2, y + 2, if (selectedAnchor == anchor) 0xFF55FF55.toInt() else if (widget.anchor == anchor) 0xFFFFFF55.toInt() else 0xFFFF5555.toInt())
                }
            }
        }

        graphics.text(MC.font, "§e[SHIFT]§7 Toggle Grid Snap ${if (snapPosition) "§a[ON]" else "§c[OFF]"}", 10, height - 10 - MC.font.lineHeight, 0xFFFFFFFF.toInt())
        graphics.text(MC.font, "§e[SCROLL]§7 Change Scale", 10, height - 10 - (MC.font.lineHeight * 2) - 2, 0xFFFFFFFF.toInt())
        graphics.text(MC.font, "§e[CTRL]§7 Change Anchor", 10, height - 10 - (MC.font.lineHeight * 3) - 4, 0xFFFFFFFF.toInt())

        super.extractRenderState(graphics, mouseX, mouseY, a)
    }


    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        selectedWidget = screenWidgets.firstOrNull {
            it.inBounds(mouseX.toInt(), mouseY.toInt())
        }

        if (anchorSelect) {
            selectedWidget?.let {
                selectedAnchor = it.getClosestAnchor(mouseX.toInt(), mouseY.toInt())
            }
        }

        super.mouseMoved(mouseX, mouseY)
    }

    override fun mouseClicked(button: MouseButtonEvent, doubled: Boolean): Boolean {
        clickedWidget = screenWidgets.firstOrNull {
            it.inBounds(button.x.toInt(), button.y.toInt())
        }

        mouseRelX = button.x.toInt() - (clickedWidget?.x ?: 0)
        mouseRelY = button.y.toInt() - (clickedWidget?.y ?: 0)

        return super.mouseClicked(button, doubled)
    }

    override fun mouseReleased(button: MouseButtonEvent): Boolean {
        clickedWidget = null

        return super.mouseReleased(button)
    }

    override fun mouseDragged(button: MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean {
        if (button.button() == 0) {
            clickedWidget?.let { widget ->
                widget.x = (button.x - mouseRelX).toInt()
                widget.y = (button.y - mouseRelY).toInt()

                if (snapPosition) {
                    widget.x = (widget.x / 5) * 5
                    widget.y = (widget.y / 5) * 5
                }

                val anchorX = (widget.anchor.x * widget.width).toInt()
                val anchorY = (widget.anchor.y * widget.height).toInt()

                widget.x = widget.x.coerceIn(
                    min(anchorX, anchorX + width - widget.width),
                    max(anchorX, anchorX + width - widget.width)
                )

                widget.y = widget.y.coerceIn(
                    min(anchorY, anchorY + height - widget.height),
                    max(anchorY, anchorY + height - widget.height)
                )
                return true
            }
        }

        return super.mouseDragged(button, offsetX, offsetY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmt: Double, verticalAmt: Double): Boolean {
        selectedWidget?.let {
            it.scale += verticalAmt.toFloat() * 0.1f
            it.scale = (round(it.scale * 10) / 10).coerceIn(0.1f, 3f) // get rid of weird floating point errors
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmt, verticalAmt)
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        snapPosition = input.hasShiftDown()
        anchorSelect = input.hasControlDown()
        return super.keyPressed(input)
    }

    override fun keyReleased(input: KeyEvent): Boolean {
        snapPosition = input.hasShiftDown()

        if (anchorSelect && !input.hasControlDown()) {
            anchorSelect = false
            selectedWidget?.let {
                it.anchor
                it.setAnchorPreservePosition(selectedAnchor ?: it.anchor)
            }
        }

        return super.keyReleased(input)
    }
}