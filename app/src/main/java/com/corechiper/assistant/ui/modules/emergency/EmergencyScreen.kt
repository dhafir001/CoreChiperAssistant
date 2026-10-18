package com.corechiper.assistant.ui.modules.emergency

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.corechiper.assistant.data.local.EmergencyProfile
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard

data class FirstAidTopic(val title: String, val steps: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(vm: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val contacts by vm.emergencyContacts.collectAsState()
    val emergencyProfile by vm.emergencyProfile.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }
    var showSosConfirmationDialog by remember { mutableStateOf(false) }
    var sosActionStatusLog by remember { mutableStateOf("Status SOS: Menunggu aktivasi pengguna.") }

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactRel by remember { mutableStateOf("Keluarga") }

    val firstAidGuides = listOf(
        FirstAidTopic("1. Pendarahan Ringan (Minor Bleeding)", "Tekan luka dengan kain bersih/kasa steril selama 5–10 menit. Bersihkan dengan air mengalir, oleskan antiseptik, dan tutup dengan plester."),
        FirstAidTopic("2. Luka Bakar (Burns)", "Aliri area luka bakar dengan air mengalir bersuhu ruang (bukan es) selama 15–20 menit. Jangan memecahkan lepuhan atau mengoleskan pasta gigi/mentega."),
        FirstAidTopic("3. Pingsan (Fainting)", "Baringkan korban di tempat teduh, tinggikan posisi kaki sekitar 30 cm di atas jantung, longgarkan pakaian yang mengikat, dan pastikan sirkulasi udara baik."),
        FirstAidTopic("4. Tersedak (Choking)", "Jika korban masih bisa batuk, dorong untuk terus batuk. Jika tidak bisa bernapas, lakukan 5 tepukan punggung (back blows) diikuti 5 hentakan perut (Heimlich maneuver)."),
        FirstAidTopic("5. Panduan Dasar RJP / CPR", "Panggil bantuan darurat 119. Letakkan pangkal telapak tangan di tengah dada korban, tekan sedalam 5–6 cm dengan kecepatan 100–120 kompresi per menit tanpa henti hingga tim medis tiba."),
        FirstAidTopic("6. Kecelakaan Lalu Lintas (Road Accidents)", "Amankan lokasi (nyalakan lampu hazard/segitiga pengaman). Jangan memindahkan korban yang dicurigai mengalami cedera leher/tulang belakang kecuali ada bahaya kebakaran."),
        FirstAidTopic("7. Bencana Alam (Natural Disasters)", "Saat gempa: Berlindung di bawah meja kokoh (Drop, Cover, Hold On), jauhi kaca, dan evakuasi ke titik kumpul terbuka setelah guncangan berhenti.")
    )

    @SuppressLint("MissingPermission")
    fun executeSosWithUserConfirmation() {
        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        var mapsLink = "Lokasi GPS tidak tersedia"
        if (hasPerm) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (loc != null) {
                mapsLink = "https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
            }
        }
        val primaryPhone = contacts.firstOrNull()?.phoneNumber ?: ""
        val messageBody = "DARURAT SOS CoreChiperAssistant! Saya membutuhkan bantuan segera. Lokasi terakhir saya: $mapsLink"
        sosActionStatusLog = "Pesan SOS telah disiapkan dengan tautan lokasi ($mapsLink). Membuka aplikasi SMS/Berbagi agar Anda dapat mengirimkannya."

        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$primaryPhone")
            putExtra("sms_body", messageBody)
        }
        runCatching {
            context.startActivity(smsIntent)
        }.onFailure {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, messageBody)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Kirim Pesan Darurat SOS"))
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        executeSosWithUserConfirmation()
    }

    fun dialEmergencyNumber(number: String) {
        // Strictly uses ACTION_DIAL so the user explicitly confirms the call
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("06. Asisten Darurat & P3K") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("SOS & Nomor") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Kontak & Medis") })
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Panduan P3K") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (tabIndex) {
                    0 -> {
                        item {
                            CoreCard {
                                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { showSosConfirmationDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        modifier = Modifier.fillMaxWidth().height(56.dp)
                                    ) {
                                        Text("AKTIFKAN DARURAT SOS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(sosActionStatusLog, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        item {
                            val prof = emergencyProfile ?: EmergencyProfile()
                            CoreCard {
                                Text("Pintasan Nomor Darurat (Indonesia Default)", fontWeight = FontWeight.Bold)
                                Text("Catatan: Ketersediaan nomor darurat dapat berbeda menurut wilayah/operator.", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(8.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { dialEmergencyNumber(prof.ambulanceNumber) }, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Call, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Ambulans (${prof.ambulanceNumber})")
                                    }
                                    OutlinedButton(onClick = { dialEmergencyNumber(prof.policeNumber) }, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Call, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Polisi (${prof.policeNumber})")
                                    }
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { dialEmergencyNumber(prof.fireNumber) }, modifier = Modifier.weight(1f)) {
                                        Text("Damkar (${prof.fireNumber})")
                                    }
                                    OutlinedButton(onClick = { dialEmergencyNumber(prof.sarNumber) }, modifier = Modifier.weight(1f)) {
                                        Text("Basarnas (${prof.sarNumber})")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            var blood by remember(emergencyProfile) { mutableStateOf(emergencyProfile?.bloodType ?: "O+") }
                            var allergy by remember(emergencyProfile) { mutableStateOf(emergencyProfile?.allergies ?: "Tidak ada") }
                            var medNote by remember(emergencyProfile) { mutableStateOf(emergencyProfile?.medicalNotes ?: "") }
                            CoreCard {
                                Text("Informasi Medis Darurat", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = blood, onValueChange = { blood = it }, label = { Text("Golongan Darah (Opsional)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = allergy, onValueChange = { allergy = it }, label = { Text("Alergi (Opsional)") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = medNote, onValueChange = { medNote = it }, label = { Text("Catatan Medis Penting") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.saveEmergencyProfile(
                                            (emergencyProfile ?: EmergencyProfile()).copy(
                                                bloodType = blood,
                                                allergies = allergy,
                                                medicalNotes = medNote
                                            )
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Profil Darurat") }
                            }
                        }

                        item {
                            CoreCard {
                                Text("Tambah Kontak Darurat", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = contactName, onValueChange = { contactName = it }, label = { Text("Nama Kontak") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = contactPhone, onValueChange = { contactPhone = it }, label = { Text("Nomor Telepon") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = contactRel, onValueChange = { contactRel = it }, label = { Text("Hubungan") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addEmergencyContact(contactName, contactPhone, contactRel, true)
                                        contactName = ""; contactPhone = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Kontak Darurat") }
                            }
                        }

                        items(contacts) { c ->
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text("${c.name} (${c.relationship})", fontWeight = FontWeight.Bold)
                                        Text(c.phoneNumber)
                                    }
                                    Row {
                                        IconButton(onClick = { dialEmergencyNumber(c.phoneNumber) }) { Icon(Icons.Default.Call, contentDescription = "Panggil") }
                                        IconButton(onClick = { vm.deleteEmergencyContact(c) }) { Icon(Icons.Default.Delete, contentDescription = "Hapus") }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            CoreCard {
                                Text(
                                    text = "Penafian Keselamatan: Panduan P3K di bawah ini bersifat edukatif offline dan tidak menggantikan pelatihan medis profesional. Segera hubungi layanan darurat 119/112 pada kondisi kritis.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        items(firstAidGuides) { guide ->
                            CoreCard {
                                Text(guide.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(4.dp))
                                Text(guide.steps, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSosConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showSosConfirmationDialog = false },
            title = { Text("Konfirmasi Aktivasi SOS") },
            text = { Text("Sistem akan mengambil koordinat GPS Anda saat ini, membuat tautan Google Maps, dan menyiapkan pesan darurat di aplikasi SMS/Berbagi Anda. Lanjutkan?") },
            confirmButton = {
                Button(
                    onClick = {
                        showSosConfirmationDialog = false
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ya, Siapkan Pesan SOS") }
            },
            dismissButton = {
                TextButton(onClick = { showSosConfirmationDialog = false }) { Text("Batal") }
            }
        )
    }
}
