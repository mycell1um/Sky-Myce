package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement
import kotlin.math.max

class TableElement(
    val children: List<List<HudElement>>,
    val columnSpacing: Int = 2,
    val rowSpacing: Int = 2,
) : HudElement() {

    private val columnWidths: List<Int> by lazy {
        val columns = children.maxOfOrNull { it.size } ?: 0

        List(columns) { column ->
            children.maxOfOrNull { row ->
                row.getOrNull(column)?.width ?: 0
            } ?: 0
        }
    }

    private val rowHeights: List<Int> by lazy {
        children.map { row ->
            row.maxOfOrNull { it.height } ?: 0
        }
    }

    override val width: Int by lazy {
        columnWidths.sum() + max(columnWidths.size - 1, 0) * columnSpacing
    }

    override val height: Int by lazy {
        rowHeights.sum() + max(rowHeights.size - 1, 0) * rowSpacing
    }

    override fun layout() {
        var yOffset = 0

        children.forEachIndexed { rowIndex, row ->
            var xOffset = 0

            row.forEachIndexed { columnIndex, child ->
                child.x = x + xOffset
                child.y = y + yOffset

                child.layout()

                xOffset += columnWidths[columnIndex] + columnSpacing
            }

            yOffset += rowHeights[rowIndex] + rowSpacing
        }
    }

    override fun render(context: HudContext) {
        children.flatten().forEach { child ->
            child.render(context)
        }
    }
}