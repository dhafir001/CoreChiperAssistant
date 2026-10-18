package com.corechiper.assistant.ui.modules.document

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(vm: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val documents by vm.documents.collectAsState()

    var extractedTextPreview by remember { mutableStateOf("") }
    var renderedPdfBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // SAF PDF / Document Picker
    val safPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch(Dispatchers.IO) {
                val destFile = File(context.filesDir, "doc_${System.currentTimeMillis()}.pdf")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output -> input.copyTo(output) }
                }
                vm.addDocumentRecord(
                    title = destFile.name,
                    category = "PDF",
                    internalPath = destFile.absolutePath,
                    ocrText = "",
                    sizeBytes = destFile.length()
                )
            }
        }
    }

    // Document Image Capture + ML Kit OCR + Save as PDF
    val captureScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            scope.launch {
                try {
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val visionText = recognizer.process(InputImage.fromBitmap(bitmap, 0)).await()
                    val recognized = visionText.text
                    extractedTextPreview = recognized

                    withContext(Dispatchers.IO) {
                        val pdf = PdfDocument()
                        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                        val page = pdf.startPage(pageInfo)
                        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                        pdf.finishPage(page)

                        val outFile = File(context.filesDir, "scan_${System.currentTimeMillis()}.pdf")
                        FileOutputStream(outFile).use { pdf.writeTo(it) }
                        pdf.close()

                        vm.addDocumentRecord(
                            title = outFile.name,
                            category = "SCAN",
                            internalPath = outFile.absolutePath,
                            ocrText = recognized,
                            sizeBytes = outFile.length()
                        )
                    }
                } catch (e: Exception) {
                    vm.showMessage("Gagal memproses OCR/PDF: ${e.message}")
                }
            }
        }
    }

    fun previewPdfFirstPage(path: String) {
        scope.launch(Dispatchers.IO) {
            runCatching {
                val file = File(path)
                if (!file.exists()) return@runCatching
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(pfd)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val bmp = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    renderedPdfBitmap = bmp
                }
                renderer.close()
                pfd.close()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("04. Dokumen, Scanner & OCR") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CoreCard {
                    Text("Penyimpanan Privat & SAF", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Dokumen yang diimpor disalin ke direktori internal privat aplikasi (filesDir) melalui Android Storage Access Framework tanpa memerlukan izin akses seluruh penyimpanan perangkat.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { captureScannerLauncher.launch(null) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Scan & OCR ke PDF")
                        }
                        FilledTonalButton(
                            onClick = { safPdfLauncher.launch(arrayOf("application/pdf")) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.UploadFile, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Impor PDF (SAF)")
                        }
                    }
                }
            }

            if (extractedTextPreview.isNotBlank()) {
                item {
                    CoreCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Hasil Ekstraksi Teks OCR", fontWeight = FontWeight.Bold)
                            IconButton(onClick = {
                                clipboard.setText(AnnotatedString(extractedTextPreview))
                                vm.showMessage("Teks OCR disalin ke papan klip")
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Salin")
                            }
                        }
                        Text(extractedTextPreview, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (renderedPdfBitmap != null) {
                item {
                    CoreCard {
                        Text("Pratinjau Halaman PDF", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Image(
                            bitmap = renderedPdfBitmap!!.asImageBitmap(),
                            contentDescription = "Pratinjau PDF",
                            modifier = Modifier.fillMaxWidth().height(260.dp)
                        )
                    }
                }
            }

            items(documents) { doc ->
                CoreCard(onClick = {
                    if (doc.extractedOcrText.isNotBlank()) extractedTextPreview = doc.extractedOcrText
                    previewPdfFirstPage(doc.internalFilePath)
                }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(doc.title, fontWeight = FontWeight.Bold)
                            Text("Kategori: ${doc.category} • ${doc.sizeBytes / 1024} KB", style = MaterialTheme.typography.bodySmall)
                            if (doc.extractedOcrText.isNotBlank()) {
                                Text("OCR: ${doc.extractedOcrText.take(60)}...", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        IconButton(onClick = { vm.deleteDocumentRecord(doc) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            }
        }
    }
}
