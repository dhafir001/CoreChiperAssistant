package com.corechiper.assistant

import com.corechiper.assistant.core.backup.BackupManager
import com.corechiper.assistant.domain.Calculators
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class CoreChiperUnitTest {

    @Test
    fun `test BMI calculation accuracy and non-diagnostic label`() {
        val result = Calculators.calculateBmi(weightKg = 68.0, heightCm = 172.0)
        assertEquals(23.0, result.bmiValue, 0.1)
        assertTrue(result.categoryLabelId.contains("Estimasi"))
        assertTrue(result.disclaimer.contains("bukan diagnosis medis"))
    }

    @Test
    fun `test precise BigDecimal financial net balance arithmetic`() {
        val incomes = listOf("5000000", "1250500")
        val expenses = listOf("1500000", "750500")
        val net = Calculators.calculateNetBalance(incomes, expenses)
        assertEquals(BigDecimal("4000000"), net)
    }

    @Test
    fun `test exercise calories MET estimation`() {
        val calories = Calculators.estimateExerciseCalories(met = 7.0, weightKg = 70.0, durationMinutes = 30)
        assertEquals(257.3, calories, 0.1)
    }

    @Test
    fun `test AES-GCM backup encryption and decryption roundtrip integrity`() {
        val sampleJson = """{"backupSchemaVersion":1,"createdAt":1720000000}"""
        val passphrase = "CoreChiperStrongPassword2026"
        val encrypted = BackupManager.encryptBackupJson(sampleJson, passphrase)
        assertNotEquals(sampleJson, String(encrypted))
        val decrypted = BackupManager.decryptBackupBytes(encrypted, passphrase)
        assertEquals(sampleJson, decrypted)
    }
}
