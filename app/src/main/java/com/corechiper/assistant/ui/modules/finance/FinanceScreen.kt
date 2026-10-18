package com.corechiper.assistant.ui.modules.finance

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.corechiper.assistant.core.util.Formatters
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.components.CoreCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(vm: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val wallets by vm.wallets.collectAsState()
    val transactions by vm.transactions.collectAsState()
    val budgets by vm.budgets.collectAsState()
    val goals by vm.savingsGoals.collectAsState()
    val debts by vm.debts.collectAsState()

    var tabIndex by remember { mutableIntStateOf(0) }

    var txType by remember { mutableStateOf("EXPENSE") }
    var txCategory by remember { mutableStateOf("Food") }
    var txAmount by remember { mutableStateOf("") }
    var txDesc by remember { mutableStateOf("") }
    var txMethod by remember { mutableStateOf("Tunai") }

    var walletName by remember { mutableStateOf("") }
    var walletInitial by remember { mutableStateOf("") }

    var budgetLimit by remember { mutableStateOf("") }
    var goalName by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }
    var debtPerson by remember { mutableStateOf("") }
    var debtAmount by remember { mutableStateOf("") }

    val incomeCategories = listOf("Salary", "Business", "Allowance", "Other income")
    val expenseCategories = listOf("Food", "Transportation", "Education", "Shopping", "Health", "Entertainment", "Bills", "Other expenses")

    fun shareExportedFile(file: java.io.File, mimeType: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Laporan Keuangan"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("02. Manajemen Keuangan (IDR)") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                },
                actions = {
                    TextButton(onClick = { vm.exportFinanceCsv { shareExportedFile(it, "text/csv") } }) { Text("CSV") }
                    TextButton(onClick = { vm.exportFinancePdf { shareExportedFile(it, "application/pdf") } }) { Text("PDF") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = tabIndex, edgePadding = 16.dp) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("Transaksi") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("Dompet") })
                Tab(selected = tabIndex == 2, onClick = { tabIndex = 2 }, text = { Text("Anggaran & Target") })
                Tab(selected = tabIndex == 3, onClick = { tabIndex = 3 }, text = { Text("Utang/Piutang") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (tabIndex) {
                    0 -> {
                        item {
                            CoreCard {
                                Text("Catat Transaksi Baru", fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = txType == "EXPENSE",
                                        onClick = { txType = "EXPENSE"; txCategory = expenseCategories.first() },
                                        label = { Text("Pengeluaran") }
                                    )
                                    FilterChip(
                                        selected = txType == "INCOME",
                                        onClick = { txType = "INCOME"; txCategory = incomeCategories.first() },
                                        label = { Text("Pemasukan") }
                                    )
                                }
                                val activeCats = if (txType == "INCOME") incomeCategories else expenseCategories
                                Text("Kategori: $txCategory", style = MaterialTheme.typography.bodySmall)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    activeCats.take(4).forEach { cat ->
                                        SuggestionChip(onClick = { txCategory = cat }, label = { Text(cat) })
                                    }
                                }
                                OutlinedTextField(
                                    value = txAmount,
                                    onValueChange = { txAmount = it },
                                    label = { Text("Nominal (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(value = txDesc, onValueChange = { txDesc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = txMethod, onValueChange = { txMethod = it }, label = { Text("Metode Pembayaran") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val wId = wallets.firstOrNull()?.id ?: 1L
                                        vm.addTransaction(wId, txType, txCategory, txAmount, txDesc, txMethod)
                                        txAmount = ""; txDesc = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Transaksi (Idempotent)") }
                            }
                        }
                        items(transactions) { tx ->
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("${tx.type} • ${tx.categoryName}", fontWeight = FontWeight.Bold)
                                        Text(Formatters.formatIdr(tx.amountRp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                        Text("${tx.description} (${tx.paymentMethod})", style = MaterialTheme.typography.bodySmall)
                                        Text(Formatters.formatDate(tx.transactionDate), style = MaterialTheme.typography.labelSmall)
                                    }
                                    IconButton(onClick = { vm.deleteTransaction(tx) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        item {
                            CoreCard {
                                Text("Buat Dompet Baru", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = walletName, onValueChange = { walletName = it }, label = { Text("Nama Dompet") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(
                                    value = walletInitial,
                                    onValueChange = { walletInitial = it },
                                    label = { Text("Saldo Awal (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addWallet(walletName, walletInitial)
                                        walletName = ""; walletInitial = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Tambah Dompet") }
                            }
                        }
                        items(wallets) { w ->
                            CoreCard {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(w.name, fontWeight = FontWeight.Bold)
                                        Text("Saldo Saat Ini: ${Formatters.formatIdr(w.currentBalanceCents)}", color = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { vm.safeDeleteWallet(w) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus Dompet")
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            CoreCard {
                                Text("Atur Anggaran Bulanan", fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = budgetLimit,
                                    onValueChange = { budgetLimit = it },
                                    label = { Text("Batas Pengeluaran Bulanan (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { vm.saveMonthlyBudget("2026-10", "ALL", budgetLimit); budgetLimit = "" },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Anggaran") }
                            }
                        }
                        items(budgets) { b ->
                            CoreCard {
                                Text("Anggaran ${b.monthYear} (${b.categoryName}): ${Formatters.formatIdr(b.limitAmountRp)}", fontWeight = FontWeight.SemiBold)
                            }
                        }
                        item {
                            CoreCard {
                                Text("Tambah Target Tabungan (Savings Goal)", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = goalName, onValueChange = { goalName = it }, label = { Text("Nama Target") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(
                                    value = goalTarget,
                                    onValueChange = { goalTarget = it },
                                    label = { Text("Target Nominal (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addSavingsGoal(goalName, goalTarget, "0", System.currentTimeMillis() + 7776000_000L)
                                        goalName = ""; goalTarget = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Target Tabungan") }
                            }
                        }
                        items(goals) { g ->
                            CoreCard {
                                Text(g.name, fontWeight = FontWeight.Bold)
                                Text("Target: ${Formatters.formatIdr(g.targetAmountRp)}")
                            }
                        }
                    }

                    3 -> {
                        item {
                            CoreCard {
                                Text("Catat Utang / Piutang", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = debtPerson, onValueChange = { debtPerson = it }, label = { Text("Nama Pihak") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(
                                    value = debtAmount,
                                    onValueChange = { debtAmount = it },
                                    label = { Text("Nominal (Rp)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        vm.addDebt(debtPerson, "BORROWED", debtAmount, System.currentTimeMillis() + 604800_000L, "")
                                        debtPerson = ""; debtAmount = ""
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Simpan Catatan Utang/Piutang") }
                            }
                        }
                        items(debts) { d ->
                            CoreCard {
                                Text("${d.personName} (${d.type})", fontWeight = FontWeight.Bold)
                                Text("Sisa Tagihan: ${Formatters.formatIdr(d.remainingAmountRp)}")
                            }
                        }
                    }
                }
            }
        }
    }
}
