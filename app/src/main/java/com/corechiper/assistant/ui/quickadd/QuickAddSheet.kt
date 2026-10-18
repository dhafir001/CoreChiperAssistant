package com.corechiper.assistant.ui.quickadd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard

@Composable
fun QuickAddScreen(vm: MainViewModel, onNavigateToModule: (String) -> Unit) {
    var selectedAction by remember { mutableStateOf("TRANSACTION") }
    val wallets by vm.wallets.collectAsState()
    val subjects by vm.subjects.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Aksi Cepat (Quick Add)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Tambahkan data langsung ke basis data tanpa berpindah menu.", style = MaterialTheme.typography.bodyMedium)

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf(
                "TRANSACTION" to "Transaksi",
                "TASK" to "Tugas",
                "HEALTH" to "Kesehatan"
            ).forEachIndexed { idx, (key, label) ->
                SegmentedButton(
                    selected = selectedAction == key,
                    onClick = { selectedAction = key },
                    shape = SegmentedButtonDefaults.itemShape(index = idx, count = 3)
                ) {
                    Text(label)
                }
            }
        }
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf(
                "NOTE" to "Catatan Belajar",
                "REMINDER" to "Pengingat",
                "DOCUMENT" to "Dokumen"
            ).forEachIndexed { idx, (key, label) ->
                SegmentedButton(
                    selected = selectedAction == key,
                    onClick = { selectedAction = key },
                    shape = SegmentedButtonDefaults.itemShape(index = idx, count = 3)
                ) {
                    Text(label)
                }
            }
        }

        CoreCard {
            when (selectedAction) {
                "TRANSACTION" -> {
                    var amount by remember { mutableStateOf("") }
                    var desc by remember { mutableStateOf("") }
                    var type by remember { mutableStateOf("EXPENSE") }
                    Text("Tambah Transaksi Cepat", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = type == "EXPENSE", onClick = { type = "EXPENSE" }, label = { Text("Pengeluaran") })
                        FilterChip(selected = type == "INCOME", onClick = { type = "INCOME" }, label = { Text("Pemasukan") })
                    }
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Nominal (IDR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Deskripsi / Kategori") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val walletId = wallets.firstOrNull()?.id ?: 1L
                            vm.addTransaction(walletId, type, if (type == "INCOME") "Salary" else "Food", amount, desc, "Tunai")
                            amount = ""; desc = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Transaksi") }
                }

                "TASK" -> {
                    var title by remember { mutableStateOf("") }
                    var priority by remember { mutableStateOf("HIGH") }
                    Text("Tambah Tugas Cepat", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Tugas") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.addTask(title, "", priority, "Umum", System.currentTimeMillis() + 86400_000L)
                            title = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Tugas") }
                }

                "HEALTH" -> {
                    var weight by remember { mutableStateOf("") }
                    var height by remember { mutableStateOf("170") }
                    Text("Tambah Rekam Kesehatan", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Berat Badan (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Tinggi Badan (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.addHealthRecord(weight.toDoubleOrNull() ?: 0.0, height.toDoubleOrNull() ?: 0.0, null, null, null, "Quick Add")
                            weight = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Rekam Kesehatan") }
                }

                "NOTE" -> {
                    var topic by remember { mutableStateOf("") }
                    var content by remember { mutableStateOf("") }
                    Text("Tambah Catatan Belajar", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Judul Topik") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Isi Catatan") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val subId = subjects.firstOrNull()?.id ?: 1L
                            vm.addStudyNote(subId, topic, content)
                            topic = ""; content = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Simpan Catatan") }
                }

                "REMINDER" -> {
                    var title by remember { mutableStateOf("") }
                    var minutes by remember { mutableStateOf("15") }
                    Text("Jadwalkan Pengingat Cepat", fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Pesan Pengingat") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it },
                        label = { Text("Dalam berapa menit dari sekarang?") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.addCustomReminder(title, "GENERAL", minutes.toLongOrNull() ?: 15L)
                            title = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Aktifkan Pengingat") }
                }

                "DOCUMENT" -> {
                    Text("Pemindaian & Impor Dokumen", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Buka Modul Dokumen untuk memindai dengan kamera, menjalankan OCR, atau memilih file PDF dari SAF.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onNavigateToModule("module_document") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Buka Modul Dokumen & Scanner")
                    }
                }
            }
        }
    }
}
