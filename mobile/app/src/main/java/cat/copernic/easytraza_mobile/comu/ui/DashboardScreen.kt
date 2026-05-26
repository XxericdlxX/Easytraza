package cat.copernic.easytraza_mobile.comu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.R
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.AsyncImagePainter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
/**
 * Pantalla principal del mobile després de seleccionar un usuari.
 *
 * Mostra les dades bàsiques de l'usuari actiu, la foto de perfil quan està
 * disponible i els accessos principals a la recepció d'albarans, la gestió de
 * lots i la configuració de connexió.
 *
 * @param currentUserName nom visible de l'usuari seleccionat.
 * @param currentUserRole rol visible de l'usuari seleccionat.
 * @param currentUserInitials inicials utilitzades com a alternativa si no hi ha foto.
 * @param currentUserPhotoUrl URL de la foto de perfil de l'usuari.
 * @param onBackToUsers acció per tornar a la selecció d'usuaris.
 * @param onOpenRecepcio acció per obrir la recepció d'albarans.
 * @param onOpenLots acció per obrir la gestió de lots.
 * @param onOpenConfig acció per obrir la configuració.
 */
@Composable
fun DashboardScreen(
    currentUserName: String,
    currentUserRole: String,
    currentUserInitials: String,
    currentUserPhotoUrl: String?,
    onBackToUsers: () -> Unit,
    onOpenRecepcio: () -> Unit,
    onOpenLots: () -> Unit,
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
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DashboardUserAvatar(
                    photoUrl = currentUserPhotoUrl,
                    initials = currentUserInitials
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_welcome_label),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = currentUserName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = currentUserRole,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }

                OutlinedButton(
                    onClick = onOpenConfig,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_config_short_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onBackToUsers,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_change_user_button),
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = stringResource(R.string.dashboard_actions_section_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        DashboardActionCard(
            emoji = "📄",
            title = stringResource(R.string.dashboard_action_receive_title),
            subtitle = stringResource(R.string.dashboard_action_receive_subtitle),
            onClick = onOpenRecepcio
        )

        DashboardActionCard(
            emoji = stringResource(R.string.dashboard_action_lots_emoji),
            title = stringResource(R.string.dashboard_action_lots_title),
            subtitle = stringResource(R.string.dashboard_action_lots_subtitle),
            onClick = onOpenLots
        )
    }
}

/**
 * Avatar de l'usuari del dashboard mobile.
 *
 * Carrega la foto de perfil si hi ha URL disponible. Si no existeix o falla la
 * càrrega de la imatge, mostra les inicials de l'usuari com a alternativa.
 *
 * @param photoUrl URL de la foto de perfil.
 * @param initials inicials de l'usuari.
 */
@Composable
private fun DashboardUserAvatar(
    photoUrl: String?,
    initials: String
) {
    val hasPhotoUrl = !photoUrl.isNullOrBlank()

    val painter = rememberAsyncImagePainter(
        model = photoUrl
    )

    val painterState by painter.state.collectAsState()
    val imageFailed = painterState is AsyncImagePainter.State.Error
    val showPhoto = hasPhotoUrl && !imageFailed

    val isLogoImage = photoUrl
        ?.lowercase()
        ?.let { url ->
            url.contains("logo") ||
                    url.contains("superadmin") ||
                    url.contains("easytraza")
        }
        ?: false

    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(
                if (showPhoto) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showPhoto) {
            Image(
                painter = painter,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isLogoImage) 7.dp else 0.dp)
                    .clip(CircleShape),
                contentScale = if (isLogoImage) {
                    ContentScale.Fit
                } else {
                    ContentScale.Crop
                }
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
/**
 * Card d'acció del dashboard mobile.
 *
 * @param emoji icona o emoji representatiu.
 * @param title títol de l'acció.
 * @param subtitle descripció breu de l'acció.
 * @param onClick acció executada en prémer la targeta.
 */
@Composable
private fun DashboardActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.displaySmall
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.dashboard_action_open),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
