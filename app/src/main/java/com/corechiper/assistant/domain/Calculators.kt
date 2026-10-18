package com.corechiper.assistant.domain

import java.math.BigDecimal
import java.math.RoundingMode

object Calculators {

    data class BmiEvaluation(
        val bmiValue: Double,
        val categoryLabelId: String,
        val categoryLabelEn: String,
        val disclaimer: String = "Catatan: BMI adalah estimasi skrining populasi, bukan diagnosis medis."
    )

    fun calculateBmi(weightKg: Double, heightCm: Double): BmiEvaluation {
        require(weightKg > 0.0 && heightCm > 0.0) { "Berat dan tinggi badan harus lebih dari 0" }
        val heightMeters = heightCm / 100.0
        val rawBmi = weightKg / (heightMeters * heightMeters)
        val roundedBmi = BigDecimal.valueOf(rawBmi).setScale(1, RoundingMode.HALF_UP).toDouble()

        val (labelId, labelEn) = when {
            roundedBmi < 18.5 -> "Berat Badan Kurang (Estimasi)" to "Underweight (Estimate)"
            roundedBmi < 25.0 -> "Rentang Normal (Estimasi)" to "Normal Range (Estimate)"
            roundedBmi < 30.0 -> "Berat Badan Lebih (Estimasi)" to "Overweight (Estimate)"
            else -> "Obesitas (Estimasi)" to "Obese (Estimate)"
        }
        return BmiEvaluation(roundedBmi, labelId, labelEn)
    }

    /**
     * Standard Compendium of Physical Activities formula:
     * Calories = MET * 3.5 * weightKg / 200 * durationMinutes
     */
    fun estimateExerciseCalories(met: Double, weightKg: Double, durationMinutes: Int): Double {
        if (met <= 0 || weightKg <= 0 || durationMinutes <= 0) return 0.0
        val raw = (met * 3.5 * weightKg / 200.0) * durationMinutes
        return BigDecimal.valueOf(raw).setScale(1, RoundingMode.HALF_UP).toDouble()
    }

    fun calculateNetBalance(incomesRp: List<String>, expensesRp: List<String>): BigDecimal {
        val totalIn = incomesRp.fold(BigDecimal.ZERO) { acc, s ->
            acc.add(s.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
        val totalOut = expensesRp.fold(BigDecimal.ZERO) { acc, s ->
            acc.add(s.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
        return totalIn.subtract(totalOut)
    }
}
