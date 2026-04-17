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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.ui.theme.Projecte4_EasyTraza_EricTheme
import cat.copernic.easytraza_mobile.ui.viewmodel.ConfigIpViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = IpPreferencesRepository(applicationContext)
        val viewModel = ConfigIpViewModel(application, repository)

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
                            text = stringResource(R.string.config_ip_title),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = stringResource(R.string.config_ip_description),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = ip,
                            onValueChange = { viewModel.onIpChange(it) },
                            label = { Text(stringResource(R.string.config_ip_label)) },
                            placeholder = { Text(stringResource(R.string.config_ip_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.saveIp() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.config_ip_save_button))
                        }

                        Button(
                            onClick = { viewModel.testConnection() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.config_ip_test_button))
                        }

                        Text(
                            text = stringResource(R.string.config_ip_status_label, status),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = stringResource(R.string.config_ip_help_emulator),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = stringResource(R.string.config_ip_help_device),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}