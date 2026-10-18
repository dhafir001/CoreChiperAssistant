package com.corechiper.assistant.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.core.util.Formatters
import com.corechiper.assistant.domain.Calculators
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard
import com.corechiper.assistant.ui.components.SectionHeader

@Composable
fun HomeScreen(
    vm: MainViewModel,
    onNavigateToModule: (String) -> Unit
) {
    val profile by vm.userProfile.collectAsState()
    val transactions by vm.transactions.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val reminders by vm.reminders.collectAsState()
    val healthRecords by vm.healthRecords.collectAsState()
    val subjects by vm.subjects.collectAsState()
    val documents by vm.documents.collectAsState()
    val notes by vm.studyNotes.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    val incomes = transactions.filter { it.type == "INCOME" }.map { it.amountRp }
    val expenses = transactions.filter { it.type == "EXPENSE" }.map { it.amountRp }
    val netBalance = Calculators.calculateNetBalance(incomes, expenses)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Halo, ${profile?.fullName ?: "Pengguna"}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = Formatters.formatDate(System.currentTimeMillis()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Global Search Bar across all modules
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari tugas, transaksi, catatan, dokumen, kesehatan...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Bersihkan")
                        }
                    }
                },
                singleLine = true
            )
        }

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            val matchedTasks = tasks.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
            val matchedTx = transactions.filter { it.description.lowercase().contains(q) || it.categoryName.lowercase().contains(q) }
            val matchedDocs = documents.filter { it.title.lowercase().contains(q) || it.extractedOcrText.lowercase().contains(q) }
            val matchedNotes = notes.filter { it.topicTitle.lowercase().contains(q) || it.content.lowercase().contains(q) }
            val matchedHealth = healthRecords.filter { it.notes.lowercase().contains(q) }

            item {
                SectionHeader("Hasil Pencarian Global")
            }
            items(matchedTasks) { t ->
                CoreCard(onClick = { onNavigateToModule("module_productivity") }) {
                    Text("[Tugas] ${t.title}", fontWeight = FontWeight.SemiBold)
                    Text(t.category, style = MaterialTheme.typography.bodySmall)
                }
            }
            items(matchedTx) { tx ->
                CoreCard(onClick = { onNavigateToModule("module_finance") }) {
                    Text("[Keuangan] ${tx.description} — ${Formatters.formatIdr(tx.amountRp)}", fontWeight = FontWeight.SemiBold)
                }
            }
            items(matchedDocs) { doc ->
                CoreCard(onClick = { onNavigateToModule("module_document") }) {
                    Text("[Dokumen] ${doc.title}", fontWeight = FontWeight.SemiBold)
                }
            }
            items(matchedNotes) { note ->
                CoreCard(onClick = { onNavigateToModule("module_learning") }) {
                    Text("[Belajar] ${note.topicTitle}", fontWeight = FontWeight.SemiBold)
                }
            }
            items(matchedHealth) { hr ->
                CoreCard(onClick = { onNavigateToModule("module_health") }) {
                    Text("[Kesehatan] ${hr.weightKg} kg — ${hr.notes}", fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            // Financial Overview
            item {
                CoreCard(onClick = { onNavigateToModule("module_finance") }) {
                    Text("Ringkasan Keuangan", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatIdr(netBalance.toPlainString()),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val totalIn = incomes.fold(java.math.BigDecimal.ZERO) { a, b -> a.add(b.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO) }
                        val totalOut = expenses.fold(java.math.BigDecimal.ZERO) { a, b -> a.add(b.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO) }
                        Text("Pemasukan: ${Formatters.formatIdr(totalIn.toPlainString())}", style = MaterialTheme.typography.bodySmall)
                        Text("Pengeluaran: ${Formatters.formatIdr(totalOut.toPlainString())}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Quick Access 6 Modules
            item {
                SectionHeader("Akses Cepat 6 Modul Utama")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { onNavigateToModule("module_health") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.FavoriteBorder, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Kesehatan")
                        }
                        FilledTonalButton(onClick = { onNavigateToModule("module_finance") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.AccountBalanceWallet, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Keuangan")
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { onNavigateToModule("module_productivity") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.CheckCircleOutline, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Produktivitas")
                        }
                        FilledTonalButton(onClick = { onNavigateToModule("module_document") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Description, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Dokumen")
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { onNavigateToModule("module_learning") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.School, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Belajar")
                        }
                        Button(
                            onClick = { onNavigateToModule("module_emergency") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.WarningAmber, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Darurat SOS")
                        }
                    }
                }
            }

            // Today's Tasks & Reminders
            item {
                val pendingTasks = tasks.filter { !it.isCompleted }.take(3)
                CoreCard(onClick = { onNavigateToModule("module_productivity") }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tugas Aktif (${tasks.count { !it.isCompleted }})", fontWeight = FontWeight.SemiBold)
                        Text("Pengingat (${reminders.size})", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(6.dp))
                    if (pendingTasks.isEmpty()) {
                        Text("Semua tugas selesai. Tambahkan tugas baru melalui tombol Tambah.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        pendingTasks.forEach { t ->
                            Text("• ${t.title} [${t.priority}]", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Health & Learning Summary Row
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CoreCard(
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToModule("module_health") }
                    ) {
                        Text("Kesehatan", fontWeight = FontWeight.SemiBold)
                        val latest = healthRecords.firstOrNull()
                        if (latest != null) {
                            val bmi = Calculators.calculateBmi(latest.weightKg, latest.heightCm)
                            Text("${latest.weightKg} kg | BMI ${bmi.bmiValue}", style = MaterialTheme.typography.bodyMedium)
                            Text(bmi.categoryLabelId, style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Belum ada data", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    CoreCard(
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToModule("module_learning") }
                    ) {
                        Text("Progres Belajar", fontWeight = FontWeight.SemiBold)
                        Text("${subjects.size} Topik Aktif", style = MaterialTheme.typography.bodyMedium)
                        Text("Target: ${profile?.dailyStudyGoalMinutes ?: 30} mnt/hari", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
