package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement
import kotlin.math.max

enum class LayoutAxis {
    VERTICAL,
    HORIZONTAL,
}

enum class LayoutType {
    START,
    CENTER,
    END
}

class StackElement(
    val orientation: LayoutAxis = LayoutAxis.VERTICAL,
    val type: LayoutType = LayoutType.START,
    val spacing: Int = 2,
    val children: List<HudElement> = listOf(),
) : HudElement() {
    val space = max(spacing * (children.size - 1), 0)

    override val width: Int by lazy {
        when (orientation) {
            LayoutAxis.HORIZONTAL -> children.sumOf { it.width } + space

            LayoutAxis.VERTICAL -> children.maxOfOrNull { it.width } ?: 0
        }
    }

    override val height: Int by lazy {
        when (orientation) {
            LayoutAxis.HORIZONTAL -> children.maxOfOrNull { it.height } ?: 0

            LayoutAxis.VERTICAL -> children.sumOf { it.height } + space
        }
    }

    override fun layout() {
        var cursor = 0

        for (child in children) {
            when (orientation) {
                LayoutAxis.HORIZONTAL -> {
                    child.x = x + cursor
                    child.y = y + when (type) {
                        LayoutType.START -> 0
                        LayoutType.CENTER -> (height - child.height) / 2
                        LayoutType.END -> height - child.height
                    }

                    cursor += child.width + spacing
                }

                LayoutAxis.VERTICAL -> {
                    child.x = x + when (type) {
                        LayoutType.START -> 0
                        LayoutType.CENTER -> (width - child.width) / 2
                        LayoutType.END -> width - child.width
                    }
                    child.y = y + cursor

                    cursor += child.height + spacing
                }
            }

            child.layout()
        }
    }

    override fun render(context: HudContext) {
        children.forEach { child ->
            child.render(context)
        }
    }
}
