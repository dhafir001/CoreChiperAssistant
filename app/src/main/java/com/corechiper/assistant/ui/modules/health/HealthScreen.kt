package com.corechiper.assistant.ui.modules.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.core.util.Formatters
import com.corechiper.assistant.domain.Calculators
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(vm: MainViewModel, onBack: () -> Unit) {
    val records by vm.healthRecords.collectAsState()
    val exercises by vm.exercises.collectAsState()
    val medications by vm.medications.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("170") }
    var sysInput by remember { mutableStateOf("") }
    var diaInput by remember { mutableStateOf("") }
    var glucoseInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    var exType by remember { mutableStateOf("Lari / Jogging") }
    var exDuration by remember { mutableStateOf("30") }
    var medName by remember { mutableStateOf("") }
    var medDosage by remember { mutableStateOf("") }
    var medFreq by remember { mutableStateOf("Sehari 1x setelah makan") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("01. Manajemen Kesehatan") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Rekam & BMI") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Olahraga") })
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Obat") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CoreCard {
                        Text(
                            text = "Penafian Medis: Modul ini hanya untuk pencatatan pribadi. Nilai BMI dan kalori adalah estimasi kalkulasi matematis, bukan diagnosis medis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (tabIndex) {
                    0 -> {
                        item {
                            val latest = records.firstOrNull()
                            if (latest != null) {
                                val bmi = Calculators.calculateBmi(latest.weightKg, latest.heightCm)
                                CoreCard {
                                    Text("Indeks Massa Tubuh (BMI) Terkini", fontWeight = FontWeight.Bold)
                                    Text("BMI: ${bmi.bmiValue} — ${bmi.categoryLabelId}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(bmi.disclaimer, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        item {
                            CoreCard {
                                Text("Catat Pengukuran Baru", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = weightInput,
                                        onValueChange = { weightInput = it },
                                        label = { Text("Berat (kg)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = heightInput,
                                        onValueChange = { heightInput = it },
                                        label = { Text("Tinggi (cm)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = sysInput,
                                        onValueChange = { sysInput = it },
                                        label = { Text("Sistolik (mmHg)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = diaInput,
                                        onValueChange = { diaInput = it },
                                        label = { Text("Diastolik (mmHg)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                OutlinedTextField(
                                    value = glucoseInput,
                                    onValueChange = { glucoseInput = it },
                                    label = { Text("Gula Darah (mg/dL - Opsional)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = notesInput,
                                    onValueChange = { notesInput = it },
                                    label = { Text("Catatan Kesehatan Pribadi") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addHealthRecord(
                                            weightInput.toDoubleOrNull() ?: 0.0,
                                            heightInput.toDoubleOrNull() ?: 0.0,
                                            sysInput.toIntOrNull(),
                                            diaInput.toIntOrNull(),
                                            glucoseInput.toIntOrNull(),
                                            notesInput
                                        )
                                        weightInput = ""; sysInput = ""; diaInput = ""; glucoseInput = ""; notesInput = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Catatan Kesehatan") }
                            }
                        }
                        items(records) { rec ->
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("${rec.weightKg} kg | ${rec.heightCm} cm", fontWeight = FontWeight.Bold)
                                        if (rec.systolicBp != null && rec.diastolicBp != null) {
                                            Text("Tekanan Darah: ${rec.systolicBp}/${rec.diastolicBp} mmHg", style = MaterialTheme.typography.bodySmall)
                                        }
                                        if (rec.bloodGlucoseMgDl != null) {
                                            Text("Glukosa: ${rec.bloodGlucoseMgDl} mg/dL", style = MaterialTheme.typography.bodySmall)
                                        }
                                        if (rec.notes.isNotBlank()) Text(rec.notes, style = MaterialTheme.typography.bodySmall)
                                        Text(Formatters.formatDate(rec.recordedAt), style = MaterialTheme.typography.labelSmall)
                                    }
                                   IconButton(onClick = { /* Hapus record kesehatan */ }) {
                                        Icon(Icons.Default.Delete, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            CoreCard {
                                Text("Catat Aktivitas Olahraga (Rumus MET Compendium)", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = exType, onValueChange = { exType = it }, label = { Text("Jenis Olahraga") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(
                                    value = exDuration,
                                    onValueChange = { exDuration = it },
                                    label = { Text("Durasi (Menit)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { vm.addExercise(exType, exDuration.toIntOrNull() ?: 30, 7.0) },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Olahraga & Hitung Estimasi Kalori") }
                            }
                        }
                        items(exercises) { ex ->
                            CoreCard {
                                Text("${ex.activityType} (${ex.durationMinutes} menit)", fontWeight = FontWeight.Bold)
                                Text("Estimasi Kalori Terbakar: ~${ex.estimatedCalories} kkal (MET ${ex.metValue})", style = MaterialTheme.typography.bodySmall)
                                Text(Formatters.formatDate(ex.exerciseDate), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    2 -> {
                        item {
                            CoreCard {
                                Text("Tambah Pengingat Obat", fontWeight = FontWeight.Bold)
                                Text("Pengingat hanya memberi tahu jadwal dan tidak mengklaim obat telah diminum secara otomatis.", style = MaterialTheme.typography.bodySmall)
                                OutlinedTextField(value = medName, onValueChange = { medName = it }, label = { Text("Nama Obat / Vitamin") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = medDosage, onValueChange = { medDosage = it }, label = { Text("Dosis (mis. 500mg / 1 Tablet)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = medFreq, onValueChange = { medFreq = it }, label = { Text("Aturan Pakai / Jadwal") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addMedication(medName, medDosage, medFreq)
                                        medName = ""; medDosage = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan & Aktifkan Notifikasi Obat") }
                            }
                        }
                        items(medications) { med ->
                            CoreCard {
                                Text("${med.name} — ${med.dosage}", fontWeight = FontWeight.Bold)
                                Text("Jadwal: ${med.frequencyDescription}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
