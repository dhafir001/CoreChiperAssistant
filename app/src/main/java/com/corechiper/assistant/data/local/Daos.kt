package com.corechiper.assistant.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface CoreDao {

    // --- USER PROFILE ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile)

    // --- HEALTH MODULE ---
    @Query("SELECT * FROM health_records ORDER BY recordedAt DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecord): Long

    @Delete
    suspend fun deleteHealthRecord(record: HealthRecord)

    @Query("SELECT * FROM exercise_records ORDER BY exerciseDate DESC")
    fun getAllExercises(): Flow<List<ExerciseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(record: ExerciseRecord)

    @Delete
    suspend fun deleteExercise(record: ExerciseRecord)

    @Query("SELECT * FROM medications ORDER BY id DESC")
    fun getAllMedications(): Flow<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("SELECT * FROM medication_schedules")
    fun getAllMedicationSchedules(): Flow<List<MedicationSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicationSchedule(schedule: MedicationSchedule)

    // --- FINANCE MODULE ---
    @Query("SELECT * FROM wallets ORDER BY id ASC")
    fun getAllWallets(): Flow<List<Wallet>>

    @Query("SELECT * FROM wallets WHERE id = :id")
    suspend fun getWalletById(id: Long): Wallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet): Long

    @Update
    suspend fun updateWallet(wallet: Wallet)

    @Query("SELECT COUNT(*) FROM transactions WHERE walletId = :walletId")
    suspend fun getTransactionCountForWallet(walletId: Long): Int

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    @Query("SELECT * FROM transaction_categories ORDER BY type, name")
    fun getAllCategories(): Flow<List<TransactionCategory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<TransactionCategory>)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransactionRaw(tx: TransactionEntity): Long

    @Delete
    suspend fun deleteTransactionRaw(tx: TransactionEntity)

    @Transaction
    suspend fun recordTransactionWithBalanceUpdate(tx: TransactionEntity) {
        val wallet = getWalletById(tx.walletId)
            ?: throw IllegalStateException("Dompet tidak ditemukan")
        val amountLong = BigDecimal(tx.amountRp).toLong()
        val updatedBalance = if (tx.type == "INCOME") {
            wallet.currentBalanceCents + amountLong
        } else {
            wallet.currentBalanceCents - amountLong
        }
        insertTransactionRaw(tx)
        updateWallet(wallet.copy(currentBalanceCents = updatedBalance))
    }

    @Transaction
    suspend fun deleteTransactionAndRestoreBalance(tx: TransactionEntity) {
        val wallet = getWalletById(tx.walletId) ?: return
        val amountLong = BigDecimal(tx.amountRp).toLong()
        val restoredBalance = if (tx.type == "INCOME") {
            wallet.currentBalanceCents - amountLong
        } else {
            wallet.currentBalanceCents + amountLong
        }
        deleteTransactionRaw(tx)
        updateWallet(wallet.copy(currentBalanceCents = restoredBalance))
    }

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: Budget)

    @Query("SELECT * FROM savings_goals ORDER BY targetDate ASC")
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSavingsGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteSavingsGoal(goal: SavingsGoal)

    @Query("SELECT * FROM debt_records ORDER BY dueDate ASC")
    fun getAllDebts(): Flow<List<DebtRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDebtRecord(debt: DebtRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtPayment(payment: DebtPayment)

    // --- PRODUCTIVITY MODULE ---
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task): Long

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM calendar_events ORDER BY eventDate ASC")
    fun getAllEvents(): Flow<List<CalendarEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(event: CalendarEvent)

    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: Habit)

    @Query("SELECT * FROM habit_logs")
    fun getAllHabitLogs(): Flow<List<HabitLog>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabitLog(log: HabitLog)

    // --- DOCUMENT MODULE ---
    @Query("SELECT * FROM document_folders ORDER BY name ASC")
    fun getAllFolders(): Flow<List<DocumentFolder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: DocumentFolder)

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)

    // --- LEARNING MODULE ---
    @Query("SELECT * FROM study_subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<StudySubject>>

    @Query("SELECT COUNT(*) FROM study_subjects")
    suspend fun getSubjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: StudySubject): Long

    @Query("SELECT * FROM study_notes ORDER BY updatedAt DESC")
    fun getAllStudyNotes(): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudyNote(note: StudyNote)

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlashcard(card: Flashcard)

    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz): Long

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Long): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions")
    fun getAllQuizQuestions(): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestions(questions: List<QuizQuestion>)

    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    // --- EMERGENCY MODULE ---
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, name ASC")
    fun getAllEmergencyContacts(): Flow<List<EmergencyContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEmergencyContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteEmergencyContact(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_profile WHERE id = 1")
    fun getEmergencyProfile(): Flow<EmergencyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEmergencyProfile(profile: EmergencyProfile)

    // --- REMINDERS ---
    @Query("SELECT * FROM reminders WHERE isActive = 1 ORDER BY triggerAtMillis ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isActive = 1")
    suspend fun getActiveRemindersSnapshot(): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}
