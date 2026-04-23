package cat.copernic.easytraza_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.ui.screen.ConfigScreen
import cat.copernic.easytraza_mobile.ui.screen.DashboardScreen
import cat.copernic.easytraza_mobile.ui.screen.RecepcioAlbaraScreen
import cat.copernic.easytraza_mobile.ui.screen.UserSelectionScreen
import cat.copernic.easytraza_mobile.ui.theme.Projecte4_EasyTraza_EricTheme
import cat.copernic.easytraza_mobile.ui.viewmodel.ConfigIpViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = IpPreferencesRepository(applicationContext)
        val configViewModel = ConfigIpViewModel(application, repository)

        setContent {
            Projecte4_EasyTraza_EricTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val ip by configViewModel.ip.collectAsState()
                    val status by configViewModel.status.collectAsState()

                    val currentScreen = remember { mutableStateOf(AppScreen.UserSelection) }
                    val currentUser = remember { mutableStateOf<DemoUser?>(null) }

                    when (currentScreen.value) {
                        AppScreen.UserSelection -> {
                            UserSelectionScreen(
                                users = demoUsers(),
                                onUserSelected = { user ->
                                    currentUser.value = user
                                    currentScreen.value = AppScreen.Dashboard
                                },
                                onOpenConfig = {
                                    currentScreen.value = AppScreen.Config
                                }
                            )
                        }

                        AppScreen.Dashboard -> {
                            DashboardScreen(
                                currentUserName = currentUser.value?.name ?: "Operari Demo",
                                currentUserRole = currentUser.value?.role ?: "Operari",
                                currentUserEmoji = currentUser.value?.emoji ?: "🧑‍🍳",
                                onBackToUsers = {
                                    currentScreen.value = AppScreen.UserSelection
                                },
                                onOpenConfig = {
                                    currentScreen.value = AppScreen.Config
                                },
                                onOpenRecepcio = {
                                    currentScreen.value = AppScreen.Recepcio
                                }
                            )
                        }

                        AppScreen.Config -> {
                            ConfigScreen(
                                configuredIp = ip,
                                connectionStatus = status,
                                onSaveIp = { configViewModel.saveIp() },
                                onTestConnection = { configViewModel.testConnection() },
                                onIpChange = { configViewModel.onIpChange(it) },
                                onBack = {
                                    currentScreen.value = if (currentUser.value != null) {
                                        AppScreen.Dashboard
                                    } else {
                                        AppScreen.UserSelection
                                    }
                                }
                            )
                        }

                        AppScreen.Recepcio -> {
                            RecepcioAlbaraScreen(
                                currentUserName = currentUser.value?.name ?: "Operari Demo",
                                onBack = {
                                    currentScreen.value = AppScreen.Dashboard
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class AppScreen {
    UserSelection,
    Dashboard,
    Config,
    Recepcio
}

data class DemoUser(
    val name: String,
    val role: String,
    val emoji: String
)

private fun demoUsers(): List<DemoUser> {
    return listOf(
        DemoUser("Eric Delgado López", "Operari", "🧑‍🍳"),
        DemoUser("Anna Serra Puig", "Operari", "👩‍🍳"),
        DemoUser("Marc Soler Vila", "Operari", "🧑‍🔧"),
        DemoUser("Laia Costa Roca", "Operari", "👩‍🔧")
    )
}