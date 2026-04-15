package cat.copernic.easytraza_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.ui.theme.Projecte4_EasyTraza_EricTheme
import cat.copernic.easytraza_mobile.ui.viewmodel.ConfigIpViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = IpPreferencesRepository(applicationContext)
        val viewModel = ConfigIpViewModel(repository)

        setContent {
            Projecte4_EasyTraza_EricTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val ip by viewModel.ip.collectAsState()
                    val status by viewModel.status.collectAsState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Configuración del servidor",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        OutlinedTextField(
                            value = ip,
                            onValueChange = { viewModel.onIpChange(it) },
                            label = { Text("IP del servidor") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { viewModel.saveIp() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar IP")
                        }

                        Button(
                            onClick = { viewModel.testConnection() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Probar conexión")
                        }

                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}