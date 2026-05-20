package cat.copernic.easytraza_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserSelectionScreen(
    users: List<MobileUsuariDto>,
    loading: Boolean,
    status: String,
    onRefreshUsers: () -> Unit,
    onUserSelected: (MobileUsuariDto) -> Unit,
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

        if (loading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = stringResource(R.string.mobile_user_loading),
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (status.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (users.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2
            ) {
                users.forEach { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .clickable { onUserSelected(user) },
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarUsuari(user = user)

                            Text(
                                text = obtenirNomVisible(user),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = obtenirRolVisible(user),
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
            }
        }

        OutlinedButton(
            onClick = onRefreshUsers,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.mobile_user_refresh_button),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
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
private fun AvatarUsuari(user: MobileUsuariDto) {
    val fotoUrl = user.fotoPerfilUrl?.trim().orEmpty()
    var fotoCarregada by remember(fotoUrl) { mutableStateOf(false) }
    var fotoError by remember(fotoUrl) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (!fotoCarregada || fotoError || fotoUrl.isBlank()) {
            Text(
                text = obtenirInicials(user),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (fotoUrl.isNotBlank() && !fotoError) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onSuccess = { fotoCarregada = true },
                onError = { fotoError = true },
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun obtenirRolVisible(user: MobileUsuariDto): String {
    return when (user.rol?.uppercase()) {
        "ADMIN" -> stringResource(R.string.mobile_user_role_admin)
        "OPERARI" -> stringResource(R.string.mobile_user_role_operari)
        else -> user.rol.orEmpty()
    }
}

private fun obtenirNomVisible(user: MobileUsuariDto): String {
    return "${user.nom.orEmpty().trim()} ${user.cognoms.orEmpty().trim()}".trim()
}

private fun obtenirInicials(user: MobileUsuariDto): String {
    val nomVisible = obtenirNomVisible(user)

    if (nomVisible.isBlank()) {
        return "ET"
    }

    val parts = nomVisible.trim().split(Regex("\\s+"))

    if (parts.size == 1) {
        return parts[0].take(3).uppercase()
    }

    return "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
}
