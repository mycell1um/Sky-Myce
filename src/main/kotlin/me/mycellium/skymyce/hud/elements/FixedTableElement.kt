package me.mycellium.skymyce.hud.elements

import me.mycellium.skymyce.hud.HudContext
import me.mycellium.skymyce.hud.HudElement

class FixedTableElement(
    val children: List<List<HudElement>>,
    val columnWidths: List<Int>,
    val rowCount: Int,
    val rowHeight: Int = 16,
    val columnSpacing: Int = 2,
    val rowSpacing: Int = 2,
) : HudElement() {
    override val width: Int = columnWidths.sum() + (columnWidths.size - 1).coerceAtLeast(0) * columnSpacing
    override val height: Int = rowCount * rowHeight + (rowCount - 1).coerceAtLeast(0) * rowSpacing

    override fun layout() {
        children.take(rowCount).forEachIndexed { rowIndex, row ->
            var xOffset = 0
            row.take(columnWidths.size).forEachIndexed { columnIndex, child ->
                child.x = x + xOffset
                child.y = y + rowIndex * (rowHeight + rowSpacing)
                child.layout()
                xOffset += columnWidths[columnIndex] + columnSpacing
            }
        }
    }

    override fun render(context: HudContext) {
        children.take(rowCount).forEachIndexed { rowIndex, row ->
            row.take(columnWidths.size).forEachIndexed { columnIndex, child ->
                context.graphics.enableScissor(
                    child.x,
                    y + rowIndex * (rowHeight + rowSpacing),
                    child.x + columnWidths[columnIndex],
                    y + rowIndex * (rowHeight + rowSpacing) + rowHeight
                )
                child.render(context)
                context.graphics.disableScissor()
            }
        }
    }
}
