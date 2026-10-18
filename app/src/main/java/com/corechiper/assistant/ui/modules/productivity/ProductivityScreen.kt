package com.corechiper.assistant.ui.modules.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductivityScreen(vm: MainViewModel, onBack: () -> Unit) {
    val tasks by vm.tasks.collectAsState()
    val habits by vm.habits.collectAsState()
    val habitLogs by vm.habitLogs.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("HIGH") }
    var habitTitle by remember { mutableStateOf("") }

    // Pomodoro State
    var pomodoroSecondsLeft by remember { mutableIntStateOf(25 * 60) }
    var pomodoroRunning by remember { mutableStateOf(false) }
    var completedPomodoros by remember { mutableIntStateOf(0) }

    LaunchedEffect(pomodoroRunning) {
        while (pomodoroRunning && pomodoroSecondsLeft > 0) {
            delay(1000L)
            pomodoroSecondsLeft -= 1
            if (pomodoroSecondsLeft == 0) {
                pomodoroRunning = false
                completedPomodoros += 1
                vm.showMessage("Sesi Fokus Pomodoro Selesai!")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("03. Produktivitas & Waktu") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Tugas") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Kebiasaan") })
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Pomodoro") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    CoreCard {
                        Text(
                            text = "Informasi Penjadwalan Android: Pengingat dijadwalkan melalui WorkManager yang hemat baterai dan dijadwalkan ulang saat perangkat dinyalakan ulang (BOOT_COMPLETED). Batasan Doze Mode Android dapat menunda alarm tidak eksak beberapa menit demi efisiensi daya.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (tabIndex) {
                    0 -> {
                        item {
                            CoreCard {
                                Text("Buat Tugas Baru", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it }, label = { Text("Judul Tugas") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = taskDesc, onValueChange = { taskDesc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                                        FilterChip(selected = taskPriority == p, onClick = { taskPriority = p }, label = { Text(p) })
                                    }
                                }
                                Button(
                                    onClick = {
                                        vm.addTask(taskTitle, taskDesc, taskPriority, "Kerja/Kuliah", System.currentTimeMillis() + 3600_000L)
                                        taskTitle = ""; taskDesc = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Tambah Tugas") }
                            }
                        }
                        items(tasks) { t ->
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = t.isCompleted, onCheckedChange = { vm.toggleTaskCompletion(t) })
                                    Column(Modifier.weight(1f)) {
                                        Text(t.title, fontWeight = FontWeight.SemiBold)
                                        Text("Prioritas: ${t.priority}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { vm.deleteTask(t) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            CoreCard {
                                Text("Pelacak Kebiasaan Harian (Habit Tracker)", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = habitTitle, onValueChange = { habitTitle = it }, label = { Text("Nama Kebiasaan") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { vm.addHabit(habitTitle); habitTitle = "" }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Tambah Kebiasaan")
                                }
                            }
                        }
                        items(habits) { h ->
                            val count = habitLogs.count { it.habitId == h.id }
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(h.title, fontWeight = FontWeight.Bold)
                                        Text("Total Selesai: $count hari", style = MaterialTheme.typography.bodySmall)
                                    }
                                    FilledTonalButton(onClick = { vm.logHabitToday(h.id) }) {
                                        Text("Ceklis Hari Ini")
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            CoreCard {
                                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Timer Fokus Pomodoro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(12.dp))
                                    val mins = pomodoroSecondsLeft / 60
                                    val secs = pomodoroSecondsLeft % 60
                                    Text(
                                        text = "%02d:%02d".format(mins, secs),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = { pomodoroRunning = !pomodoroRunning }) {
                                            Text(if (pomodoroRunning) "Jeda" else "Mulai Fokus")
                                        }
                                        OutlinedButton(onClick = { pomodoroRunning = false; pomodoroSecondsLeft = 25 * 60 }) {
                                            Text("Reset 25m")
                                        }
                                        OutlinedButton(onClick = { pomodoroRunning = false; pomodoroSecondsLeft = 5 * 60 }) {
                                            Text("Istirahat 5m")
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text("Sesi Selesai Hari Ini: $completedPomodoros")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
