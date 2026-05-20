package cat.copernic.easytraza_mobile

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto
import cat.copernic.easytraza_mobile.ui.screen.ConfigScreen
import cat.copernic.easytraza_mobile.ui.screen.DashboardScreen
import cat.copernic.easytraza_mobile.ui.screen.GestioLotsScreen
import cat.copernic.easytraza_mobile.ui.screen.RecepcioAlbaraScreen
import cat.copernic.easytraza_mobile.ui.screen.UserSelectionScreen
import cat.copernic.easytraza_mobile.ui.theme.Projecte4_EasyTraza_EricTheme
import cat.copernic.easytraza_mobile.ui.viewmodel.ConfigIpViewModel
import cat.copernic.easytraza_mobile.ui.viewmodel.GestioLotsViewModel
import cat.copernic.easytraza_mobile.ui.viewmodel.RecepcioAlbaraViewModel
import cat.copernic.easytraza_mobile.ui.viewmodel.UserSelectionViewModel

/**
 * Component mobile `MainActivity` de l'aplicació mobile d'EasyTraza.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = IpPreferencesRepository(applicationContext)
        val configViewModel = ConfigIpViewModel(application, repository)
        val userSelectionViewModel = UserSelectionViewModel(application, repository)

        setContent {
            Projecte4_EasyTraza_EricTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val ip by configViewModel.ip.collectAsState()
                    val status by configViewModel.status.collectAsState()
                    val users by userSelectionViewModel.users.collectAsState()
                    val loadingUsers by userSelectionViewModel.loading.collectAsState()
                    val userStatus by userSelectionViewModel.status.collectAsState()

                    val currentScreen = remember { mutableStateOf(AppScreen.UserSelection) }
                    val currentUser = remember { mutableStateOf<MobileUsuariDto?>(null) }

                    val currentUserName = construirNomVisible(currentUser.value)
                    val currentUserRole = obtenirRolVisible(currentUser.value?.rol)
                    val currentUserInitials = obtenirInicials(currentUser.value)

                    when (currentScreen.value) {
                        AppScreen.UserSelection -> {
                            UserSelectionScreen(
                                users = users,
                                loading = loadingUsers,
                                status = userStatus,
                                onRefreshUsers = {
                                    userSelectionViewModel.carregarUsuaris()
                                },
                                onUserSelected = { user: MobileUsuariDto ->
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
                                currentUserName = currentUserName,
                                currentUserRole = currentUserRole,
                                currentUserInitials = currentUserInitials,
                                onBackToUsers = {
                                    currentUser.value = null
                                    userSelectionViewModel.carregarUsuaris()
                                    currentScreen.value = AppScreen.UserSelection
                                },
                                onOpenConfig = {
                                    currentScreen.value = AppScreen.Config
                                },
                                onOpenRecepcio = {
                                    currentScreen.value = AppScreen.Recepcio
                                },
                                onOpenLots = {
                                    currentScreen.value = AppScreen.Lots
                                }
                            )
                        }

                        AppScreen.Lots -> {
                            val gestioLotsViewModel: GestioLotsViewModel = viewModel()

                            GestioLotsScreen(
                                viewModel = gestioLotsViewModel,
                                onBack = {
                                    currentScreen.value = AppScreen.Dashboard
                                }
                            )
                        }

                        AppScreen.Config -> {
                            ConfigScreen(
                                configuredIp = ip,
                                connectionStatus = status,
                                onSaveIp = { configViewModel.saveIp() },
                                onTestConnection = { configViewModel.testConnection() },
                                onIpChange = { newValue: String ->
                                    configViewModel.onIpChange(newValue)
                                },
                                onBack = {
                                    currentScreen.value =
                                        if (currentUser.value != null) AppScreen.Dashboard
                                        else AppScreen.UserSelection

                                    if (currentUser.value == null) {
                                        userSelectionViewModel.carregarUsuaris()
                                    }
                                }
                            )
                        }

                        AppScreen.Recepcio -> {
                            val recepcioViewModel: RecepcioAlbaraViewModel = viewModel()

                            RecepcioAlbaraScreen(
                                currentUserId = currentUser.value?.id,
                                currentUserName = currentUserName,
                                viewModel = recepcioViewModel,
                                onBack = {
                                    currentScreen.value = AppScreen.Dashboard
                                },
                                onSaveSuccess = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.recepcio_save_success_toast),
                                        Toast.LENGTH_SHORT
                                    ).show()

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

/**
 * Component mobile `AppScreen` de l'aplicació mobile d'EasyTraza.
 */
private enum class AppScreen {
    UserSelection,
    Dashboard,
    Config,
    Recepcio,
    Lots
}

/**
 * Executa l'operació `construirNomVisible`.
 * @param user paràmetre necessari per a l'operació.
 * @return resultat obtingut després d'executar l'operació.
 */
private fun construirNomVisible(user: MobileUsuariDto?): String {
    if (user == null) {
        return ""
    }

    val complet = "${user.nom.orEmpty().trim()} ${user.cognoms.orEmpty().trim()}".trim()
    return complet.ifBlank { "#${user.id}" }
}

/**
 * Executa l'operació `obtenirRolVisible`.
 * @param rol paràmetre necessari per a l'operació.
 * @return resultat obtingut després d'executar l'operació.
 */
@androidx.compose.runtime.Composable
private fun obtenirRolVisible(rol: String?): String {
    return when (rol?.uppercase()) {
        "ADMIN" -> stringResource(R.string.mobile_user_role_admin)
        "OPERARI" -> stringResource(R.string.mobile_user_role_operari)
        else -> rol.orEmpty()
    }
}

/**
 * Executa l'operació `obtenirInicials`.
 * @param user paràmetre necessari per a l'operació.
 * @return resultat obtingut després d'executar l'operació.
 */
private fun obtenirInicials(user: MobileUsuariDto?): String {
    val nomVisible = construirNomVisible(user)

    if (nomVisible.isBlank()) {
        return "ET"
    }

    val parts = nomVisible.trim().split(Regex("\\s+"))

    if (parts.size == 1) {
        return parts[0].take(2).uppercase()
    }

    return "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
}
