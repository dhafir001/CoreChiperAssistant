package com.corechiper.assistant.ui.insights

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.core.util.Formatters
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard
import java.math.BigDecimal

@Composable
fun InsightsScreen(vm: MainViewModel) {
    val transactions by vm.transactions.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val healthRecords by vm.healthRecords.collectAsState()
    val flashcards by vm.flashcards.collectAsState()
    val quizResults by vm.quizResults.collectAsState()

    val totalIncome = transactions.filter { it.type == "INCOME" }
        .fold(BigDecimal.ZERO) { acc, t -> acc.add(t.amountRp.toBigDecimalOrNull() ?: BigDecimal.ZERO) }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }
        .fold(BigDecimal.ZERO) { acc, t -> acc.add(t.amountRp.toBigDecimalOrNull() ?: BigDecimal.ZERO) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Statistik & Analitik", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Seluruh grafik dihitung secara dinamis dari basis data nyata Anda.", style = MaterialTheme.typography.bodySmall)

        // Finance Chart
        CoreCard {
            Text("Perbandingan Pemasukan vs Pengeluaran", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            val maxVal = maxOf(totalIncome.toFloat(), totalExpense.toFloat(), 1f)
            Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                val barHeight = 32f
                val inWidth = (totalIncome.toFloat() / maxVal) * size.width
                val outWidth = (totalExpense.toFloat() / maxVal) * size.width
                drawRect(color = primaryColor, topLeft = Offset(0f, 8f), size = Size(inWidth.coerceAtLeast(8f), barHeight))
                drawRect(color = secondaryColor, topLeft = Offset(0f, 50f), size = Size(outWidth.coerceAtLeast(8f), barHeight))
            }
            Text("Pemasukan: ${Formatters.formatIdr(totalIncome.toPlainString())}", style = MaterialTheme.typography.bodySmall)
            Text("Pengeluaran: ${Formatters.formatIdr(totalExpense.toPlainString())}", style = MaterialTheme.typography.bodySmall)
        }

        // Task Completion Rate
        CoreCard {
            val totalTasks = tasks.size
            val completedTasks = tasks.count { it.isCompleted }
            val ratio = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
            Text("Penyelesaian Tugas Produktivitas", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { ratio }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            Text("$completedTasks dari $totalTasks tugas selesai (${(ratio * 100).toInt()}%)")
        }

        // Health Weight Trend
        CoreCard {
            Text("Tren Berat Badan (${healthRecords.size} Catatan)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            if (healthRecords.size < 2) {
                Text("Tambahkan minimal 2 catatan kesehatan untuk melihat grafik tren berat badan.", style = MaterialTheme.typography.bodySmall)
            } else {
                val chronological = healthRecords.take(10).reversed()
                val minW = chronological.minOf { it.weightKg }.toFloat() - 2f
                val maxW = chronological.maxOf { it.weightKg }.toFloat() + 2f
                Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    val stepX = size.width / (chronological.size - 1).coerceAtLeast(1)
                    for (i in 0 until chronological.size - 1) {
                        val y1 = size.height - ((chronological[i].weightKg.toFloat() - minW) / (maxW - minW)) * size.height
                        val y2 = size.height - ((chronological[i + 1].weightKg.toFloat() - minW) / (maxW - minW)) * size.height
                        drawLine(
                            color = primaryColor,
                            start = Offset(i * stepX, y1),
                            end = Offset((i + 1) * stepX, y2),
                            strokeWidth = 5f
                        )
                    }
                }
            }
        }

        // Learning Progress
        CoreCard {
            val learnedCards = flashcards.count { it.isLearned }
            val avgQuiz = if (quizResults.isNotEmpty()) quizResults.map { it.scorePercent }.average().toInt() else 0
            Text("Statistik Pembelajaran", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Flashcard Dikuasai: $learnedCards / ${flashcards.size}")
            Text("Rata-rata Skor Kuis: $avgQuiz% (${quizResults.size} percobaan)")
        }
    }
}
