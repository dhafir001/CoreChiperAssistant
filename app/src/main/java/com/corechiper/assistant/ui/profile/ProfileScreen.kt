package com.corechiper.assistant.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.core.backup.BackupManager
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(vm: MainViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profile by vm.userProfile.collectAsState()
    val prefs by vm.preferences.collectAsState()

    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "Pengguna CoreChiper") }
    var studyMinutes by remember(profile) { mutableStateOf((profile?.dailyStudyGoalMinutes ?: 30).toString()) }
    var backupPassphrase by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val encryptedBytes = withContext(Dispatchers.IO) {
                        BackupManager.createEncryptedBackup(vm.dao, backupPassphrase)
                    }
                    context.contentResolver.openOutputStream(uri)?.use { it.write(encryptedBytes) }
                    vm.showMessage("Cadangan terenkripsi AES-GCM berhasil diekspor")
                } catch (e: Exception) {
                    vm.showMessage("Gagal mengekspor cadangan: ${e.message}")
                }
            }
        }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.readBytes() ?: ByteArray(0)
                    }
                    val count = withContext(Dispatchers.IO) {
                        BackupManager.restoreEncryptedBackup(vm.dao, bytes, backupPassphrase)
                    }
                    vm.showMessage("Berhasil memulihkan $count entitas data dari cadangan")
                } catch (e: Exception) {
                    vm.showMessage("Gagal memulihkan cadangan (Kata sandi salah atau file rusak): ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Profil & Pengaturan Sistem", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        CoreCard {
            Text("Informasi Pribadi", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = studyMinutes, onValueChange = { studyMinutes = it }, label = { Text("Target Belajar Harian (Menit)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.updateUserProfile(fullName, studyMinutes.toIntOrNull() ?: 30) }, modifier = Modifier.fillMaxWidth()) {
                Text("Simpan Profil")
            }
        }

        CoreCard {
            Text("Tampilan & Bahasa", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = prefs.themeMode == "LIGHT", onClick = { vm.setThemeMode("LIGHT") }, label = { Text("Terang") })
                FilterChip(selected = prefs.themeMode == "DARK", onClick = { vm.setThemeMode("DARK") }, label = { Text("Gelap") })
                FilterChip(selected = prefs.themeMode == "SYSTEM", onClick = { vm.setThemeMode("SYSTEM") }, label = { Text("Sistem") })
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = prefs.languageCode == "id", onClick = { vm.setLanguage("id") }, label = { Text("Bahasa Indonesia") })
                FilterChip(selected = prefs.languageCode == "en", onClick = { vm.setLanguage("en") }, label = { Text("English") })
            }
        }

        CoreCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Kunci Biometrik Aplikasi", fontWeight = FontWeight.Bold)
                    Text("Wajibkan sidik jari/kredensial perangkat saat membuka aplikasi dan dokumen", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = prefs.biometricEnabled, onCheckedChange = { vm.setBiometricLock(it) })
            }
        }

        CoreCard {
            Text("Cadangkan & Pulihkan (Enkripsi AES-256-GCM)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = backupPassphrase,
                onValueChange = { backupPassphrase = it },
                label = { Text("Kata Sandi Enkripsi Cadangan (Min. 6 Karakter)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { exportBackupLauncher.launch("corechiper_backup.ccb") },
                    modifier = Modifier.weight(1f),
                    enabled = backupPassphrase.length >= 6
                ) { Text("Ekspor Cadangan") }
                OutlinedButton(
                    onClick = { importBackupLauncher.launch(arrayOf("*/*")) },
                    modifier = Modifier.weight(1f),
                    enabled = backupPassphrase.length >= 6
                ) { Text("Pulihkan") }
            }
        }

        CoreCard {
            Text("Manajemen Data", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        context.cacheDir.deleteRecursively()
                        vm.showMessage("Cache aplikasi berhasil dibersihkan")
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Bersihkan Cache") }
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) { Text("Hapus Semua Data") }
            }
        }

        CoreCard {
            Text("Tentang CoreChiperAssistant", fontWeight = FontWeight.Bold)
            Text("Pengembang: CoreChiperLabs", style = MaterialTheme.typography.bodySmall)
            Text("Versi: 1.0.0 (API 26+ Native Kotlin Compose)", style = MaterialTheme.typography.bodySmall)
            Text("Tagline: One App. Your Entire Life.", style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Konfirmasi Penghapusan Data") },
            text = { Text("Apakah Anda yakin ingin menghapus seluruh data di semua modul secara permanen? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        vm.clearAllUserData()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ya, Hapus Permanen") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
            }
        )
    }
}
