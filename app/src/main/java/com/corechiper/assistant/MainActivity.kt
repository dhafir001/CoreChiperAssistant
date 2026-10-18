package com.corechiper.assistant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corechiper.assistant.core.navigation.CoreChiperMainScaffold
import com.corechiper.assistant.core.security.BiometricHelper
import com.corechiper.assistant.core.theme.CoreChiperTheme
import com.corechiper.assistant.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by viewModel.preferences.collectAsState()
            var isUnlocked by remember { mutableStateOf(false) }

            LaunchedEffect(prefs.biometricEnabled) {
                if (!prefs.biometricEnabled) {
                    isUnlocked = true
                } else if (BiometricHelper.canAuthenticate(this@MainActivity)) {
                    BiometricHelper.authenticate(
                        activity = this@MainActivity,
                        title = "Kunci Keamanan CoreChiperAssistant",
                        subtitle = "Verifikasi identitas Anda untuk membuka brankas kehidupan pribadi",
                        onSuccess = { isUnlocked = true },
                        onError = { isUnlocked = false }
                    )
                } else {
                    isUnlocked = true
                }
            }

            CoreChiperTheme(themeMode = prefs.themeMode) {
                if (isUnlocked) {
                    CoreChiperMainScaffold(vm = viewModel)
                } else {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CoreChiperAssistant Terkunci", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                BiometricHelper.authenticate(
                                    activity = this@MainActivity,
                                    title = "Kunci Keamanan CoreChiperAssistant",
                                    subtitle = "Gunakan biometrik atau PIN perangkat",
                                    onSuccess = { isUnlocked = true },
                                    onError = {}
                                )
                            }) {
                                Text("Buka Kunci Biometrik")
                            }
                        }
                    }
                }
            }
        }
    }
}
