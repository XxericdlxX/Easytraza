package cat.copernic.easytraza_mobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

                    EasyTrazaMobileApp(
                        configuredIp = ip,
                        connectionStatus = status,
                        onSaveIp = { viewModel.saveIp() },
                        onTestConnection = { viewModel.testConnection() },
                        onIpChange = { viewModel.onIpChange(it) }
                    )
                }
            }
        }
    }
}

private sealed class AppScreen {
    data object UserSelection : AppScreen()
    data class Dashboard(val user: DemoUser) : AppScreen()
    data class Config(val user: DemoUser?) : AppScreen()
}

private data class DemoUser(
    val name: String,
    val role: String,
    val emoji: String
)

private data class DashboardAction(
    val title: String,
    val subtitle: String,
    val emoji: String
)

@Composable
private fun EasyTrazaMobileApp(
    configuredIp: String,
    connectionStatus: String,
    onSaveIp: () -> Unit,
    onTestConnection: () -> Unit,
    onIpChange: (String) -> Unit
) {
    val screenState = remember { mutableStateOf<AppScreen>(AppScreen.UserSelection) }

    val demoUsers = listOf(
        DemoUser("Eric Delgado López", "Operari", "🧑‍🍳"),
        DemoUser("Anna Serra Puig", "Operari", "👩‍🍳"),
        DemoUser("Marc Soler Vila", "Operari", "🧑‍🔧"),
        DemoUser("Laia Costa Roca", "Operari", "👩‍🔧")
    )

    when (val screen = screenState.value) {
        is AppScreen.UserSelection -> {
            UserSelectionScreen(
                users = demoUsers,
                onUserSelected = { user ->
                    screenState.value = AppScreen.Dashboard(user)
                },
                onOpenConfig = {
                    screenState.value = AppScreen.Config(null)
                }
            )
        }

        is AppScreen.Dashboard -> {
            MobileDashboardScreen(
                currentUser = screen.user,
                onBackToUsers = {
                    screenState.value = AppScreen.UserSelection
                },
                onOpenConfig = {
                    screenState.value = AppScreen.Config(screen.user)
                }
            )
        }

        is AppScreen.Config -> {
            ConfigScreen(
                configuredIp = configuredIp,
                connectionStatus = connectionStatus,
                onSaveIp = onSaveIp,
                onTestConnection = onTestConnection,
                onIpChange = onIpChange,
                onBack = {
                    val user = screen.user
                    screenState.value = if (user != null) {
                        AppScreen.Dashboard(user)
                    } else {
                        AppScreen.UserSelection
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserSelectionScreen(
    users: List<DemoUser>,
    onUserSelected: (DemoUser) -> Unit,
    onOpenConfig: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.user_selector_emoji),
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = stringResource(R.string.user_selector_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(R.string.user_selector_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Text(
            text = stringResource(R.string.user_selector_section_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            users.forEach { user ->
                UserCard(
                    user = user,
                    modifier = Modifier.fillMaxWidth(0.48f),
                    onClick = { onUserSelected(user) }
                )
            }
        }

        OutlinedButton(
            onClick = onOpenConfig,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.user_selector_config_button),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun UserCard(
    user: DemoUser,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = user.role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.user_selector_open_profile),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MobileDashboardScreen(
    currentUser: DemoUser,
    onBackToUsers: () -> Unit,
    onOpenConfig: () -> Unit
) {
    val context = LocalContext.current

    val actions = listOf(
        DashboardAction(
            title = stringResource(R.string.dashboard_action_receive_title),
            subtitle = stringResource(R.string.dashboard_action_receive_subtitle),
            emoji = "📦"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DashboardHeader(
            currentUser = currentUser,
            onBackToUsers = onBackToUsers,
            onOpenConfig = onOpenConfig
        )

        DashboardHeroCard()

        Text(
            text = stringResource(R.string.dashboard_actions_section_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 1
        ) {
            actions.forEach { action ->
                val pendingMessage = stringResource(
                    R.string.dashboard_action_pending,
                    action.title
                )

                DashboardActionCard(
                    action = action,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        Toast.makeText(
                            context,
                            pendingMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    currentUser: DemoUser,
    onBackToUsers: () -> Unit,
    onOpenConfig: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_welcome_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentUser.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentUser.role,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(
                onClick = onOpenConfig,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.dashboard_config_short_button),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        OutlinedButton(
            onClick = onBackToUsers,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_change_user_button),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DashboardHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_hero_emoji),
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = stringResource(R.string.dashboard_hero_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = stringResource(R.string.dashboard_hero_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    action: DashboardAction,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = action.emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = action.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.dashboard_action_open),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ConfigScreen(
    configuredIp: String,
    connectionStatus: String,
    onSaveIp: () -> Unit,
    onTestConnection: () -> Unit,
    onIpChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.config_back_button),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.dashboard_config_section_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        ConfigSectionCard(
            configuredIp = configuredIp,
            connectionStatus = connectionStatus,
            onSaveIp = onSaveIp,
            onTestConnection = onTestConnection,
            onIpChange = onIpChange
        )
    }
}

@Composable
private fun ConfigSectionCard(
    configuredIp: String,
    connectionStatus: String,
    onSaveIp: () -> Unit,
    onTestConnection: () -> Unit,
    onIpChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_config_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.dashboard_config_section_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = configuredIp,
                onValueChange = onIpChange,
                label = { Text(stringResource(R.string.config_ip_label)) },
                placeholder = { Text(stringResource(R.string.config_ip_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = onSaveIp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.config_ip_save_button),
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onTestConnection,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.config_ip_test_button),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = stringResource(R.string.config_ip_status_label, connectionStatus),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}