package com.corechiper.assistant.core.backup

import com.corechiper.assistant.data.local.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Serializable
data class BackupPayload(
    val backupSchemaVersion: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val healthRecords: List<HealthRecord> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val notes: List<StudyNote> = emptyList(),
    val emergencyContacts: List<EmergencyContact> = emptyList()
)

object BackupManager {
    private const val PBKDF2_ITERATIONS = 65536
    private const val SALT_BYTES = 16
    private const val IV_BYTES = 12
    private const val GCM_TAG_BITS = 128

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun encryptBackupJson(plainTextJson: String, passphrase: String): ByteArray {
        require(passphrase.length >= 6) { "Kata sandi cadangan minimal 6 karakter" }
        val random = SecureRandom()
        val salt = ByteArray(SALT_BYTES).also { random.nextBytes(it) }
        val iv = ByteArray(IV_BYTES).also { random.nextBytes(it) }

        val secretKey = deriveAesKey(passphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
        val cipherBytes = cipher.doFinal(plainTextJson.toByteArray(Charsets.UTF_8))

        return salt + iv + cipherBytes
    }

    fun decryptBackupBytes(encryptedData: ByteArray, passphrase: String): String {
        require(encryptedData.size > SALT_BYTES + IV_BYTES) { "File cadangan rusak atau tidak valid" }
        val salt = encryptedData.copyOfRange(0, SALT_BYTES)
        val iv = encryptedData.copyOfRange(SALT_BYTES, SALT_BYTES + IV_BYTES)
        val cipherBytes = encryptedData.copyOfRange(SALT_BYTES + IV_BYTES, encryptedData.size)

        val secretKey = deriveAesKey(passphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
        val plainBytes = cipher.doFinal(cipherBytes)
        return String(plainBytes, Charsets.UTF_8)
    }

    private fun deriveAesKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    suspend fun createEncryptedBackup(dao: CoreDao, passphrase: String): ByteArray {
        val payload = BackupPayload(
            backupSchemaVersion = 1,
            healthRecords = dao.getAllHealthRecords().first(),
            wallets = dao.getAllWallets().first(),
            transactions = dao.getAllTransactions().first(),
            tasks = dao.getAllTasks().first(),
            notes = dao.getAllStudyNotes().first(),
            emergencyContacts = dao.getAllEmergencyContacts().first()
        )
        val serialized = json.encodeToString(payload)
        return encryptBackupJson(serialized, passphrase)
    }

    suspend fun restoreEncryptedBackup(dao: CoreDao, encryptedData: ByteArray, passphrase: String): Int {
        val rawJson = decryptBackupBytes(encryptedData, passphrase)
        val payload = json.decodeFromString<BackupPayload>(rawJson)
        if (payload.backupSchemaVersion > 1) {
            throw IllegalStateException("Versi cadangan (${payload.backupSchemaVersion}) tidak kompatibel dengan aplikasi ini.")
        }
        var restoredCount = 0
        payload.healthRecords.forEach { dao.insertHealthRecord(it); restoredCount++ }
        payload.wallets.forEach { dao.insertWallet(it); restoredCount++ }
        payload.tasks.forEach { dao.upsertTask(it); restoredCount++ }
        payload.emergencyContacts.forEach { dao.upsertEmergencyContact(it); restoredCount++ }
        return restoredCount
    }
}
