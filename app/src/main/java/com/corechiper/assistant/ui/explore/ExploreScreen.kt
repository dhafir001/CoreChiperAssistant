package com.corechiper.assistant.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.ui.components.CoreCard

data class ModuleItem(
    val route: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun ExploreScreen(onNavigateToModule: (String) -> Unit) {
    val modules = listOf(
        ModuleItem("module_health", "01. Kesehatan", "BMI, Rekam Medis, Olahraga & Pengingat Obat", Icons.Default.Favorite),
        ModuleItem("module_finance", "02. Keuangan", "Dompet, Transaksi IDR, Anggaran, Tabungan & PDF/CSV", Icons.Default.AccountBalanceWallet),
        ModuleItem("module_productivity", "03. Produktivitas", "Tugas, Kalender, Kebiasaan & Timer Pomodoro", Icons.Default.TaskAlt),
        ModuleItem("module_document", "04. Dokumen", "Pustaka PDF, Scanner CameraX & Ekstraksi Teks OCR", Icons.Default.FolderShared),
        ModuleItem("module_learning", "05. Pembelajaran", "Materi Offline, Flashcard Interaktif & Kuis Evaluasi", Icons.Default.MenuBook),
        ModuleItem("module_emergency", "06. Asisten Darurat", "Alur SOS Lokasi, Kontak Darurat & Panduan P3K", Icons.Default.HealthAndSafety)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Jelajah Sistem CoreChiper",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enam modul terintegrasi untuk mengelola seluruh aspek kehidupan Anda secara offline.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { item ->
                CoreCard(onClick = { onNavigateToModule(item.route) }) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
