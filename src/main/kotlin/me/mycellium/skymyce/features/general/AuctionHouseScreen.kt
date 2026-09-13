package me.mycellium.skymyce.features.general

import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.UIComponents
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.container.UIContainers
import io.wispforest.owo.ui.core.HorizontalAlignment
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.OwoUIAdapter
import io.wispforest.owo.ui.core.ParentUIComponent
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import io.wispforest.owo.ui.core.UIComponent
import io.wispforest.owo.ui.core.VerticalAlignment
import me.mycellium.skymyce.api.AuctionApi
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID
import kotlin.math.ceil

class AuctionHouseScreen : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> = OwoUIAdapter.create(this, UIContainers::verticalFlow)

    override fun build(root: FlowLayout) {
        root.surface(Surface.blur(3.0f, 10.0f))
        root.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        root.child(mainLayout())
    }

    private fun mainLayout(): UIComponent {
        val grid = UIContainers.grid(Sizing.fill(), Sizing.content(), ceil(visibleAuctions.size / 9f).toInt(), 9)
        grid.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        grid.padding(Insets.of(5))
        grid.surface(Surface.PANEL_INSET)

        visibleAuctions.forEachIndexed { index, auction ->
            grid.child(UIComponents.item(auction.item).setTooltipFromStack(true), index / 9, index % 9)
        }

        val layout = UIContainers.verticalFlow(Sizing.fill(60), Sizing.fill(60))
        layout.padding(Insets.of(10))
        layout.surface(Surface.DARK_PANEL)

        val inputField = UIComponents.textBox(Sizing.fill())
        inputField.onChanged().subscribe { value ->
            refreshAuctions(value)
        }
        layout.child(inputField)
        layout.child(UIContainers.verticalScroll(Sizing.expand(), Sizing.expand(), grid).padding(Insets.of(3)))
        return layout
    }

    private var visibleAuctions: List<AuctionApi.ActiveAuction> = emptyList()

    private fun refreshAuctions(search: String) {
        visibleAuctions = AuctionApi.auctions
            .filter { !it.expired }
            .filter { !(it.item.hoverName.stripped.contains(search, true)) }
    }

    enum class AuctionSort {
        LOWEST_BIN,
        HIGHEST_BIN,
        ENDING_SOON,
        NEWEST,
        OLDEST
    }
}