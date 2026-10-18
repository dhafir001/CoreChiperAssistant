package com.corechiper.assistant.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Pengguna CoreChiper",
    val birthYear: Int = 2000,
    val preferredCurrency: String = "IDR",
    val dailyStudyGoalMinutes: Int = 30
)

@Serializable
@Entity(tableName = "health_records", indices = [Index("recordedAt")])
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Double,
    val heightCm: Double,
    val systolicBp: Int? = null,
    val diastolicBp: Int? = null,
    val bloodGlucoseMgDl: Int? = null,
    val waistCm: Double? = null,
    val notes: String = "",
    val recordedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "exercise_records", indices = [Index("exerciseDate")])
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String,
    val durationMinutes: Int,
    val metValue: Double,
    val estimatedCalories: Double,
    val exerciseDate: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val frequencyDescription: String,
    val isActive: Boolean = true
)

@Serializable
@Entity(
    tableName = "medication_schedules",
    foreignKeys = [ForeignKey(
        entity = Medication::class,
        parentColumns = ["id"],
        childColumns = ["medicationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("medicationId")]
)
data class MedicationSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicationId: Long,
    val hourOfDay: Int,
    val minute: Int,
    val lastAcknowledgedDate: Long? = null
)

@Serializable
@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val initialBalanceCents: Long,
    val currentBalanceCents: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "transaction_categories")
data class TransactionCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "INCOME" or "EXPENSE"
    val isDefault: Boolean = false
)

@Serializable
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("walletId"), Index("transactionDate"), Index(value = ["idempotencyKey"], unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val walletId: Long,
    val type: String, // "INCOME" or "EXPENSE"
    val categoryName: String,
    val amountRp: String, // Exact BigDecimal plain string representation
    val description: String,
    val paymentMethod: String,
    val transactionDate: Long = System.currentTimeMillis(),
    val idempotencyKey: String
)

@Serializable
@Entity(tableName = "budgets", indices = [Index(value = ["monthYear", "categoryName"], unique = true)])
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthYear: String, // Format: "YYYY-MM"
    val categoryName: String, // "ALL" for overall monthly budget
    val limitAmountRp: String
)

@Serializable
@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmountRp: String,
    val currentAmountRp: String,
    val targetDate: Long
)

@Serializable
@Entity(tableName = "debt_records")
data class DebtRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val type: String, // "BORROWED" or "LENT"
    val totalAmountRp: String,
    val remainingAmountRp: String,
    val dueDate: Long,
    val notes: String = ""
)

@Serializable
@Entity(
    tableName = "debt_payments",
    foreignKeys = [ForeignKey(
        entity = DebtRecord::class,
        parentColumns = ["id"],
        childColumns = ["debtId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("debtId")]
)
data class DebtPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val debtId: Long,
    val amountRp: String,
    val paidAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "tasks", indices = [Index("dueDate"), Index("isCompleted")])
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long,
    val priority: String, // "LOW", "MEDIUM", "HIGH"
    val category: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

@Serializable
@Entity(tableName = "calendar_events", indices = [Index("eventDate")])
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val eventDate: Long
)

@Serializable
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "habit_logs",
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["habitId", "dateEpochDay"], unique = true)]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateEpochDay: Long
)

@Serializable
@Entity(tableName = "study_subjects")
data class StudySubject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val progressPercent: Int = 0
)

@Serializable
@Entity(
    tableName = "study_notes",
    foreignKeys = [ForeignKey(
        entity = StudySubject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subjectId")]
)
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val topicTitle: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "flashcards",
    foreignKeys = [ForeignKey(
        entity = StudySubject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subjectId")]
)
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val frontText: String,
    val backText: String,
    val isLearned: Boolean = false,
    val lastReviewedAt: Long? = null
)

@Serializable
@Entity(
    tableName = "quizzes",
    foreignKeys = [ForeignKey(
        entity = StudySubject::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("subjectId")]
)
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String
)

@Serializable
@Entity(
    tableName = "quiz_questions",
    foreignKeys = [ForeignKey(
        entity = Quiz::class,
        parentColumns = ["id"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("quizId")]
)
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0..3
    val explanation: String
)

@Serializable
@Entity(
    tableName = "quiz_results",
    foreignKeys = [ForeignKey(
        entity = Quiz::class,
        parentColumns = ["id"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("quizId")]
)
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val scorePercent: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "document_folders")
data class DocumentFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "documents",
    foreignKeys = [ForeignKey(
        entity = DocumentFolder::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("folderId"), Index("title")]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long? = null,
    val title: String,
    val category: String, // "PDF", "SCAN", "IMAGE", "OTHER"
    val internalFilePath: String,
    val extractedOcrText: String = "",
    val sizeBytes: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isPrimary: Boolean = false
)

@Serializable
@Entity(tableName = "emergency_profile")
data class EmergencyProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalNotes: String = "",
    val ambulanceNumber: String = "119",
    val policeNumber: String = "110",
    val fireNumber: String = "113",
    val sarNumber: String = "115"
)

@Serializable
@Entity(tableName = "reminders", indices = [Index("triggerAtMillis")])
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val moduleType: String, // "TASK", "MEDICATION", "STUDY", "HABIT", "GENERAL"
    val triggerAtMillis: Long,
    val isRepeatingDaily: Boolean = false,
    val isActive: Boolean = true
)
