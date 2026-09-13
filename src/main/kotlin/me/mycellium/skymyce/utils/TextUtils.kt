package me.mycellium.skymyce.utils

import java.util.*
import kotlin.math.abs

object TextUtils {
    fun parseProfit(value: Number, margin: Number = 10000.0): String {
        val v = value.toDouble()
        val m = margin.toDouble()

        return "${if (abs(v) < m) "§7" else if (v > 0) "§a+" else "§c-"}${NumberUtils.condense(abs(v))}"
    }

    fun romanToInt(roman: String): Int {
        val values = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
        var total = 0
        var previous = 0
        roman.uppercase(Locale.ROOT).reversed().forEach { char ->
            val value = values[char] ?: 0
            if (value < previous) total -= value else total += value
            previous = value
        }
        return total
    }
}
