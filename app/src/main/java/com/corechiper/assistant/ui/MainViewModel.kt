package com.corechiper.assistant.ui

import android.app.Application
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.corechiper.assistant.core.notification.NotificationHelper
import com.corechiper.assistant.data.local.*
import com.corechiper.assistant.data.preferences.AppPreferences
import com.corechiper.assistant.data.preferences.UserPreferencesRepository
import com.corechiper.assistant.domain.Calculators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val db = AppDatabase.getInstance(application)
    val dao = db.coreDao()
    private val prefsRepo = UserPreferencesRepository(application)

    val preferences: StateFlow<AppPreferences> = prefsRepo.preferencesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppPreferences()
    )

    val userProfile = dao.getUserProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())
    val healthRecords = dao.getAllHealthRecords().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val exercises = dao.getAllExercises().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val medications = dao.getAllMedications().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallets = dao.getAllWallets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = dao.getAllTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val budgets = dao.getAllBudgets().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savingsGoals = dao.getAllSavingsGoals().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val debts = dao.getAllDebts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks = dao.getAllTasks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habits = dao.getAllHabits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habitLogs = dao.getAllHabitLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val reminders = dao.getActiveReminders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents = dao.getAllDocuments().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val folders = dao.getAllFolders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjects = dao.getAllSubjects().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val studyNotes = dao.getAllStudyNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val flashcards = dao.getAllFlashcards().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quizzes = dao.getAllQuizzes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quizQuestions = dao.getAllQuizQuestions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val quizResults = dao.getAllQuizResults().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyContacts = dao.getAllEmergencyContacts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val emergencyProfile = dao.getEmergencyProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmergencyProfile())

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun showMessage(msg: String) {
        _statusMessage.value = msg
    }

    // --- HEALTH ACTIONS ---
    fun addHealthRecord(weightKg: Double, heightCm: Double, sys: Int?, dia: Int?, glucose: Int?, notes: String) {
        if (weightKg <= 0 || heightCm <= 0) {
            showMessage("Berat dan tinggi badan harus valid (> 0)")
            return
        }
        viewModelScope.launch {
            dao.insertHealthRecord(
                HealthRecord(
                    weightKg = weightKg,
                    heightCm = heightCm,
                    systolicBp = sys,
                    diastolicBp = dia,
                    bloodGlucoseMgDl = glucose,
                    notes = notes
                )
            )
            showMessage("Catatan kesehatan berhasil disimpan")
        }
    }

    fun addExercise(activityType: String, durationMinutes: Int, metValue: Double) {
        viewModelScope.launch {
            val latestWeight = healthRecords.value.firstOrNull()?.weightKg ?: 65.0
            val estCal = Calculators.estimateExerciseCalories(metValue, latestWeight, durationMinutes)
            dao.insertExercise(
                ExerciseRecord(
                    activityType = activityType,
                    durationMinutes = durationMinutes,
                    metValue = metValue,
                    estimatedCalories = estCal
                )
            )
            showMessage("Olahraga dicatat (~$estCal kkal)")
        }
    }

    fun addMedication(name: String, dosage: String, frequency: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = dao.insertMedication(Medication(name = name, dosage = dosage, frequencyDescription = frequency))
            val trigger = System.currentTimeMillis() + 3600_000L
            val remId = dao.upsertReminder(Reminder(title = "Minum Obat: $name ($dosage)", moduleType = "MEDICATION", triggerAtMillis = trigger))
            NotificationHelper.scheduleReminderWork(getApplication(), remId, "Pengingat Obat: $name ($dosage)", "MEDICATION", trigger)
            showMessage("Pengingat obat dijadwalkan (Catatan: Tidak mengklaim obat otomatis diminum)")
        }
    }

    // --- FINANCE ACTIONS ---
    fun addTransaction(walletId: Long, type: String, category: String, amountRp: String, description: String, paymentMethod: String) {
        val bd = amountRp.toBigDecimalOrNull()
        if (bd == null || bd <= java.math.BigDecimal.ZERO) {
            showMessage("Nominal transaksi tidak valid")
            return
        }
        viewModelScope.launch {
            try {
                val tx = TransactionEntity(
                    walletId = walletId,
                    type = type,
                    categoryName = category,
                    amountRp = bd.toPlainString(),
                    description = description.ifBlank { category },
                    paymentMethod = paymentMethod,
                    idempotencyKey = UUID.randomUUID().toString()
                )
                dao.recordTransactionWithBalanceUpdate(tx)
                showMessage("Transaksi berhasil disimpan")
            } catch (e: Exception) {
                showMessage("Gagal menyimpan transaksi: ${e.message}")
            }
        }
    }

    fun deleteTransaction(tx: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransactionAndRestoreBalance(tx)
            showMessage("Transaksi dihapus & saldo dompet diperbarui")
        }
    }

    fun addWallet(name: String, initialRp: String) {
        val bd = initialRp.toBigDecimalOrNull() ?: return
        viewModelScope.launch {
            dao.insertWallet(Wallet(name = name, initialBalanceCents = bd.toLong(), currentBalanceCents = bd.toLong()))
            showMessage("Dompet '$name' dibuat")
        }
    }

    fun safeDeleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            val count = dao.getTransactionCountForWallet(wallet.id)
            if (count > 0) {
                showMessage("Dompet tidak dapat dihapus karena memiliki $count transaksi terkait.")
            } else {
                dao.deleteWallet(wallet)
                showMessage("Dompet dihapus")
            }
        }
    }

    fun saveMonthlyBudget(monthYear: String, category: String, limitRp: String) {
        val bd = limitRp.toBigDecimalOrNull() ?: return
        viewModelScope.launch {
            dao.upsertBudget(Budget(monthYear = monthYear, categoryName = category, limitAmountRp = bd.toPlainString()))
            showMessage("Anggaran disimpan")
        }
    }

    fun addSavingsGoal(name: String, targetRp: String, currentRp: String, targetDate: Long) {
        viewModelScope.launch {
            dao.upsertSavingsGoal(
                SavingsGoal(
                    name = name,
                    targetAmountRp = targetRp,
                    currentAmountRp = currentRp,
                    targetDate = targetDate
                )
            )
            showMessage("Target tabungan disimpan")
        }
    }

    fun addDebt(personName: String, type: String, amountRp: String, dueDate: Long, notes: String) {
        viewModelScope.launch {
            dao.upsertDebtRecord(
                DebtRecord(
                    personName = personName,
                    type = type,
                    totalAmountRp = amountRp,
                    remainingAmountRp = amountRp,
                    dueDate = dueDate,
                    notes = notes
                )
            )
            showMessage("Catatan utang/piutang disimpan")
        }
    }

    fun exportFinanceCsv(onExportReady: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(getApplication<Application>().cacheDir, "laporan_keuangan_corechiper.csv")
            file.bufferedWriter().use { out ->
                out.write("ID,Tanggal,Tipe,Kategori,Nominal_IDR,Metode,Deskripsi\n")
                transactions.value.forEach { t ->
                    out.write("${t.id},${t.transactionDate},${t.type},${t.categoryName},${t.amountRp},${t.paymentMethod},\"${t.description}\"\n")
                }
            }
            onExportReady(file)
        }
    }

    fun exportFinancePdf(onExportReady: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val pdf = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint().apply { textSize = 12f }
            canvas.drawText("LAPORAN KEUANGAN - CORECHIPERASSISTANT", 40f, 40f, paint)
            var y = 75f
            transactions.value.take(30).forEach { tx ->
                canvas.drawText("${tx.type} | ${tx.categoryName} | Rp ${tx.amountRp} | ${tx.description}", 40f, y, paint)
                y += 20f
            }
            pdf.finishPage(page)
            val file = File(getApplication<Application>().cacheDir, "laporan_keuangan_corechiper.pdf")
            FileOutputStream(file).use { pdf.writeTo(it) }
            pdf.close()
            onExportReady(file)
        }
    }

    // --- PRODUCTIVITY ACTIONS ---
    fun addTask(title: String, description: String, priority: String, category: String, dueDateMillis: Long) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.upsertTask(
                Task(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    dueDate = dueDateMillis
                )
            )
            val remId = dao.upsertReminder(Reminder(title = "Tugas: $title", moduleType = "TASK", triggerAtMillis = dueDateMillis))
            NotificationHelper.scheduleReminderWork(getApplication(), remId, "Pengingat Tugas: $title", "TASK", dueDateMillis)
            showMessage("Tugas ditambahkan & pengingat dijadwalkan")
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val nowCompleted = !task.isCompleted
            dao.upsertTask(task.copy(isCompleted = nowCompleted, completedAt = if (nowCompleted) System.currentTimeMillis() else null))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { dao.deleteTask(task) }
    }

    fun addHabit(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.upsertHabit(Habit(title = title))
            showMessage("Kebiasaan baru ditambahkan")
        }
    }

    fun logHabitToday(habitId: Long) {
        viewModelScope.launch {
            val todayEpoch = LocalDate.now().toEpochDay()
            dao.insertHabitLog(HabitLog(habitId = habitId, dateEpochDay = todayEpoch))
            showMessage("Progres kebiasaan hari ini tercatat")
        }
    }

    fun addCustomReminder(title: String, moduleType: String, delayMinutes: Long) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val trigger = System.currentTimeMillis() + (delayMinutes * 60_000L)
            val id = dao.upsertReminder(Reminder(title = title, moduleType = moduleType, triggerAtMillis = trigger))
            NotificationHelper.scheduleReminderWork(getApplication(), id, title, moduleType, trigger)
            showMessage("Pengingat dijadwalkan untuk $delayMinutes menit lagi")
        }
    }

    // --- DOCUMENT ACTIONS ---
    fun addDocumentRecord(title: String, category: String, internalPath: String, ocrText: String, sizeBytes: Long) {
        viewModelScope.launch {
            dao.upsertDocument(
                DocumentEntity(
                    title = title,
                    category = category,
                    internalFilePath = internalPath,
                    extractedOcrText = ocrText,
                    sizeBytes = sizeBytes
                )
            )
            showMessage("Dokumen berhasil disimpan di penyimpanan internal")
        }
    }

    fun deleteDocumentRecord(doc: DocumentEntity) {
        viewModelScope.launch {
            runCatching { File(doc.internalFilePath).delete() }
            dao.deleteDocument(doc)
            showMessage("Dokumen dihapus")
        }
    }

    // --- LEARNING ACTIONS ---
    fun addStudySubject(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insertSubject(StudySubject(title = title, description = description))
            showMessage("Mata pelajaran ditambahkan")
        }
    }

    fun addStudyNote(subjectId: Long, topicTitle: String, content: String) {
        if (topicTitle.isBlank()) return
        viewModelScope.launch {
            dao.upsertStudyNote(StudyNote(subjectId = subjectId, topicTitle = topicTitle, content = content))
            showMessage("Catatan belajar disimpan")
        }
    }

    fun addFlashcard(subjectId: Long, front: String, back: String) {
        if (front.isBlank() || back.isBlank()) return
        viewModelScope.launch {
            dao.upsertFlashcard(Flashcard(subjectId = subjectId, frontText = front, backText = back))
            showMessage("Flashcard ditambahkan")
        }
    }

    fun toggleFlashcardLearned(card: Flashcard) {
        viewModelScope.launch {
            dao.upsertFlashcard(card.copy(isLearned = !card.isLearned, lastReviewedAt = System.currentTimeMillis()))
        }
    }

    fun recordQuizScore(quizId: Long, scorePercent: Int) {
        viewModelScope.launch {
            dao.insertQuizResult(QuizResult(quizId = quizId, scorePercent = scorePercent))
            showMessage("Skor kuis ($scorePercent%) berhasil disimpan")
        }
    }

    // --- EMERGENCY ACTIONS ---
    fun addEmergencyContact(name: String, phone: String, relation: String, isPrimary: Boolean) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            dao.upsertEmergencyContact(
                EmergencyContact(name = name, phoneNumber = phone, relationship = relation, isPrimary = isPrimary)
            )
            showMessage("Kontak darurat disimpan")
        }
    }

    fun deleteEmergencyContact(contact: EmergencyContact) {
        viewModelScope.launch { dao.deleteEmergencyContact(contact) }
    }

    fun saveEmergencyProfile(profile: EmergencyProfile) {
        viewModelScope.launch {
            dao.upsertEmergencyProfile(profile)
            showMessage("Profil medis & nomor darurat diperbarui")
        }
    }

    // --- PROFILE & SYSTEM SETTINGS ---
    fun updateUserProfile(fullName: String, dailyGoal: Int) {
        viewModelScope.launch {
            dao.upsertUserProfile(UserProfile(id = 1, fullName = fullName, dailyStudyGoalMinutes = dailyGoal))
            showMessage("Profil pengguna diperbarui")
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { prefsRepo.setThemeMode(mode) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { prefsRepo.setLanguage(code) }
    }

    fun setBiometricLock(enabled: Boolean) {
        viewModelScope.launch { prefsRepo.setBiometricEnabled(enabled) }
    }

    fun clearAllUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            db.clearAllTables()
            showMessage("Seluruh data aplikasi telah dihapus secara permanen")
        }
    }
}
