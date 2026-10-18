package com.corechiper.assistant.core.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    private val idrFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun formatIdr(amountPlainString: String): String {
        val bd = amountPlainString.toBigDecimalOrNull() ?: BigDecimal.ZERO
        return idrFormat.format(bd)
    }

    fun formatIdr(amountLong: Long): String {
        return idrFormat.format(amountLong)
    }

    fun formatDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        return sdf.format(Date(epochMillis))
    }

    fun formatShortDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(epochMillis))
    }
}
