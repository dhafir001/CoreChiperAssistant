package com.corechiper.assistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        HealthRecord::class,
        ExerciseRecord::class,
        Medication::class,
        MedicationSchedule::class,
        Wallet::class,
        TransactionCategory::class,
        TransactionEntity::class,
        Budget::class,
        SavingsGoal::class,
        DebtRecord::class,
        DebtPayment::class,
        Task::class,
        CalendarEvent::class,
        Habit::class,
        HabitLog::class,
        StudySubject::class,
        StudyNote::class,
        Flashcard::class,
        Quiz::class,
        QuizQuestion::class,
        QuizResult::class,
        DocumentFolder::class,
        DocumentEntity::class,
        EmergencyContact::class,
        EmergencyProfile::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coreDao(): CoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Safe schema evolution hook preserving all user records
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "corechiper_assistant.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialSystemAndLearningContent(instance.coreDao())
                }
                instance
            }
        }

        private suspend fun seedInitialSystemAndLearningContent(dao: CoreDao) {
            if (dao.getSubjectCount() == 0) {
                // Seed default Finance Categories & Primary Wallet (0 balance, not fake user transactions)
                dao.insertWallet(
                    Wallet(id = 1, name = "Dompet Utama (Tunai)", initialBalanceCents = 0L, currentBalanceCents = 0L)
                )
                val defaultCategories = listOf(
                    TransactionCategory(name = "Salary", type = "INCOME", isDefault = true),
                    TransactionCategory(name = "Business", type = "INCOME", isDefault = true),
                    TransactionCategory(name = "Allowance", type = "INCOME", isDefault = true),
                    TransactionCategory(name = "Other income", type = "INCOME", isDefault = true),
                    TransactionCategory(name = "Food", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Transportation", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Education", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Shopping", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Health", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Entertainment", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Bills", type = "EXPENSE", isDefault = true),
                    TransactionCategory(name = "Other expenses", type = "EXPENSE", isDefault = true)
                )
                dao.insertCategories(defaultCategories)

                // Seed Required Indonesian Learning Materials (Requirement 9F)
                val subject1Id = dao.insertSubject(
                    StudySubject(
                        title = "Literasi Keuangan & Manajemen Risiko",
                        description = "Panduan fundamental pengelolaan arus kas pribadi, dana darurat, dan perencanaan anggaran.",
                        progressPercent = 25
                    )
                )
                dao.upsertStudyNote(
                    StudyNote(
                        subjectId = subject1Id,
                        topicTitle = "Prinsip Dana Darurat & Aturan 50/30/20",
                        content = "Aturan 50/30/20 membagi pendapatan bersih menjadi: 50% Kebutuhan Pokok, 30% Keinginan, dan 20% Tabungan/Investasi. Dana darurat minimal 3–6 kali pengeluaran bulanan wajib disimpan pada instrumen likuid dan aman."
                    )
                )
                dao.upsertFlashcard(
                    Flashcard(
                        subjectId = subject1Id,
                        frontText = "Apa fungsi utama Dana Darurat?",
                        backText = "Melindungi stabilitas finansial saat terjadi kondisi darurat medis, kehilangan pekerjaan, atau perbaikan mendesak tanpa berutang."
                    )
                )
                dao.upsertFlashcard(
                    Flashcard(
                        subjectId = subject1Id,
                        frontText = "Mengapa perhitungan uang di perangkat lunak harus memakai BigDecimal?",
                        backText = "Tipe floating-point biner (Double/Float) memiliki ketidakakuratan pembulatan pecahan desimal, sedangkan BigDecimal menjamin presisi eksak."
                    )
                )
                val quizId = dao.insertQuiz(
                    Quiz(subjectId = subject1Id, title = "Evaluasi Dasar Keuangan Pribadi")
                )
                dao.insertQuizQuestions(
                    listOf(
                        QuizQuestion(
                            quizId = quizId,
                            question = "Berapa porsi ideal untuk kebutuhan pokok menurut metode anggaran 50/30/20?",
                            optionA = "20% dari pendapatan bersih",
                            optionB = "30% dari pendapatan bersih",
                            optionC = "50% dari pendapatan bersih",
                            optionD = "70% dari pendapatan bersih",
                            correctOptionIndex = 2,
                            explanation = "Metode 50/30/20 mengalokasikan 50% untuk kebutuhan esensial (pangan, tempat tinggal, transportasi)."
                        ),
                        QuizQuestion(
                            quizId = quizId,
                            question = "Manakah instrumen paling tepat untuk menyimpan dana darurat?",
                            optionA = "Aset kripto spekulatif",
                            optionB = "Rekening tabungan terpisah atau pasar uang yang likuid",
                            optionC = "Properti tidak likuid",
                            optionD = "Saham gorengan",
                            correctOptionIndex = 1,
                            explanation = "Dana darurat harus memiliki likuiditas tinggi (mudah dicairkan kapan saja) dan risiko fluktuasi nilai yang sangat rendah."
                        )
                    )
                )

                val subject2Id = dao.insertSubject(
                    StudySubject(
                        title = "Keamanan Siber & Privasi Data Pribadi",
                        description = "Praktik terbaik enkripsi, autentikasi biometrik, dan perlindungan dokumen digital.",
                        progressPercent = 10
                    )
                )
                dao.upsertStudyNote(
                    StudyNote(
                        subjectId = subject2Id,
                        topicTitle = "Enkripsi Simetris AES-GCM",
                        content = "AES-GCM (Galois/Counter Mode) menyediakan kerahasiaan sekaligus integritas autentikasi data (Authenticated Encryption with Associated Data - AEAD), mencegah modifikasi file cadangan tanpa izin."
                    )
                )
            }
        }
    }
}
