package me.mycellium.skymyce.utils

import java.text.NumberFormat
import java.util.*

object NumberUtils {
    val formatter: NumberFormat = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT)
    init {
        formatter.maximumFractionDigits = 2
    }

    fun condense(num: Number): String = formatter.format(num)
}
