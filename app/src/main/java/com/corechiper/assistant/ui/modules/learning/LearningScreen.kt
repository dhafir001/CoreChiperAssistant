package com.corechiper.assistant.ui.modules.learning

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(vm: MainViewModel, onBack: () -> Unit) {
    val subjects by vm.subjects.collectAsState()
    val notes by vm.studyNotes.collectAsState()
    val flashcards by vm.flashcards.collectAsState()
    val questions by vm.quizQuestions.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var flippedCardIds by remember { mutableStateOf(setOf<Long>()) }

    var selectedQuestionIdx by remember { mutableIntStateOf(0) }
    var selectedAnswerIdx by remember { mutableStateOf<Int?>(null) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("05. Manajemen Pembelajaran") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Materi & Catatan") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Flashcard") })
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Kuis Interaktif") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (tabIndex) {
                    0 -> {
                        items(subjects) { sub ->
                            CoreCard {
                                Text(sub.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(sub.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        items(notes) { n ->
                            CoreCard {
                                Text(n.topicTitle, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(4.dp))
                                Text(n.content, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    1 -> {
                        items(flashcards) { card ->
                            val isFlipped = flippedCardIds.contains(card.id)
                            CoreCard(
                                onClick = {
                                    flippedCardIds = if (isFlipped) flippedCardIds - card.id else flippedCardIds + card.id
                                }
                            ) {
                                Text(
                                    text = if (isFlipped) "JAWABAN (BELAKANG):" else "PERTANYAAN (DEPAN) — Ketuk untuk membalik:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = if (isFlipped) card.backText else card.frontText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(8.dp))
                                AssistChip(
                                    onClick = { vm.toggleFlashcardLearned(card) },
                                    label = { Text(if (card.isLearned) "Sudah Dikuasai ✓" else "Tandai Sudah Dikuasai") }
                                )
                            }
                        }
                    }

                    2 -> {
                        if (questions.isNotEmpty()) {
                            val q = questions[selectedQuestionIdx.coerceIn(0, questions.size - 1)]
                            item {
                                CoreCard {
                                    Text("Pertanyaan ${selectedQuestionIdx + 1} dari ${questions.size}", style = MaterialTheme.typography.labelMedium)
                                    Spacer(Modifier.height(6.dp))
                                    Text(q.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(10.dp))
                                    val opts = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
                                    opts.forEachIndexed { idx, optText ->
                                        OutlinedButton(
                                            onClick = {
                                                selectedAnswerIdx = idx
                                                if (idx == q.correctOptionIndex) correctAnswersCount += 1
                                            },
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                        ) {
                                            Text(optText)
                                        }
                                    }
                                    if (selectedAnswerIdx != null) {
                                        Spacer(Modifier.height(8.dp))
                                        val isCorrect = selectedAnswerIdx == q.correctOptionIndex
                                        Text(
                                            text = if (isCorrect) "Benar! ${q.explanation}" else "Kurang tepat. ${q.explanation}",
                                            color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                if (selectedQuestionIdx + 1 < questions.size) {
                                                    selectedQuestionIdx += 1
                                                    selectedAnswerIdx = null
                                                } else {
                                                    val pct = ((correctAnswersCount.toDouble() / questions.size) * 100).toInt().coerceIn(0, 100)
                                                    vm.recordQuizScore(q.quizId, pct)
                                                    selectedQuestionIdx = 0
                                                    selectedAnswerIdx = null
                                                    correctAnswersCount = 0
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (selectedQuestionIdx + 1 < questions.size) "Soal Berikutnya" else "Selesaikan & Simpan Skor")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
