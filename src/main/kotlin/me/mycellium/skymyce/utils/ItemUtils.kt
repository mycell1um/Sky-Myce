package me.mycellium.skymyce.utils

import me.mycellium.skymyce.config.instances.dungeons.DungeonsConfig.AuctionType
import me.mycellium.skymyce.config.instances.dungeons.DungeonsConfig.BazaarType
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.LowestBinAPI

object ItemUtils {
    fun SkyBlockId.getPrice(bazaar: BazaarType, auction: AuctionType): Double {
        val bazaarProduct = BazaarAPI.getProduct(this.bazaarId)
        if (bazaarProduct != null) {
            return when (bazaar) {
                BazaarType.INSTANT_BUY -> bazaarProduct.buyPrice
                BazaarType.INSTANT_SELL -> bazaarProduct.sellPrice
            }
        }

        val auctionProduct = LowestBinAPI.getPrice(this.bazaarId)
        if (auctionProduct != null) {
            return when (auction) {
                AuctionType.LOWEST -> auctionProduct.lowest
                AuctionType.HIGHEST -> auctionProduct.highest
                AuctionType.MEAN -> auctionProduct.mean
                AuctionType.MEDIAN -> auctionProduct.median
            }.toDouble()
        }

        return 0.0
    }
}