package cat.copernic.easytraza_mobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import cat.copernic.easytraza_mobile.ui.viewmodel.GestioLotsViewModel

private enum class LotAction {
    Iniciar,
    Finalitzar
}

@Composable
fun GestioLotsScreen(
    viewModel: GestioLotsViewModel,
    onBack: () -> Unit
) {
    val lots by viewModel.lots.collectAsState()
    val status by viewModel.status.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val filtreCodi by viewModel.filtreCodi.collectAsState()
    val filtreMateria by viewModel.filtreMateria.collectAsState()
    val filtreDataRecepcio by viewModel.filtreDataRecepcio.collectAsState()
    val filtreEstat by viewModel.filtreEstat.collectAsState()

    val pendingLot = remember { mutableStateOf<MobileLotDto?>(null) }
    val pendingAction = remember { mutableStateOf<LotAction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.carregarLots()
    }

    if (pendingLot.value != null && pendingAction.value != null) {
        val lot = pendingLot.value!!
        val action = pendingAction.value!!

        AlertDialog(
            onDismissRequest = {
                pendingLot.value = null
                pendingAction.value = null
            },
            title = {
                Text(
                    text = when (action) {
                        LotAction.Iniciar -> stringResource(R.string.lots_mobile_start_confirm_title)
                        LotAction.Finalitzar -> stringResource(R.string.lots_mobile_finish_confirm_title)
                    }
                )
            },
            text = {
                Text(
                    text = when (action) {
                        LotAction.Iniciar -> stringResource(R.string.lots_mobile_start_confirm_text)
                        LotAction.Finalitzar -> stringResource(R.string.lots_mobile_finish_confirm_text)
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (action) {
                            LotAction.Iniciar -> viewModel.iniciarLot(lot.id)
                            LotAction.Finalitzar -> viewModel.finalitzarLot(lot.id)
                        }

                        pendingLot.value = null
                        pendingAction.value = null
                    }
                ) {
                    Text(stringResource(R.string.lots_mobile_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingLot.value = null
                        pendingAction.value = null
                    }
                ) {
                    Text(stringResource(R.string.lots_mobile_cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.config_back_button),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

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
                    text = "🔁",
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = stringResource(R.string.lots_mobile_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(R.string.lots_mobile_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.lots_mobile_filters_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = filtreCodi,
                    onValueChange = viewModel::actualitzarFiltreCodi,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.lots_mobile_filter_code)) }
                )

                OutlinedTextField(
                    value = filtreMateria,
                    onValueChange = viewModel::actualitzarFiltreMateria,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.lots_mobile_filter_material)) }
                )

                OutlinedTextField(
                    value = filtreDataRecepcio,
                    onValueChange = viewModel::actualitzarFiltreDataRecepcio,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.lots_mobile_filter_date)) },
                    supportingText = { Text(stringResource(R.string.lots_mobile_filter_date_help)) }
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.lots_mobile_filter_status),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_filter_all),
                            selected = filtreEstat.isBlank(),
                            onClick = { viewModel.actualitzarFiltreEstat("") },
                            modifier = Modifier.weight(1f)
                        )
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_status_stock),
                            selected = filtreEstat == "EN_ESTOC",
                            onClick = { viewModel.actualitzarFiltreEstat("EN_ESTOC") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_status_open),
                            selected = filtreEstat == "OBERT",
                            onClick = { viewModel.actualitzarFiltreEstat("OBERT") },
                            modifier = Modifier.weight(1f)
                        )
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_status_finished),
                            selected = filtreEstat == "ACABAT",
                            onClick = { viewModel.actualitzarFiltreEstat("ACABAT") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                TextButton(
                    onClick = viewModel::netejarFiltres,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.lots_mobile_filter_clear))
                }
            }
        }

        Button(
            onClick = { viewModel.carregarLots() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = if (loading) {
                    stringResource(R.string.lots_mobile_loading)
                } else {
                    stringResource(R.string.lots_mobile_refresh)
                },
                fontWeight = FontWeight.Bold
            )
        }

        if (status.isNotBlank()) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (lots.isEmpty() && !loading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = stringResource(R.string.lots_mobile_empty),
                    modifier = Modifier.padding(18.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        lots.forEach { lot ->
            LotMobileCard(
                lot = lot,
                onIniciar = {
                    pendingLot.value = lot
                    pendingAction.value = LotAction.Iniciar
                },
                onFinalitzar = {
                    pendingLot.value = lot
                    pendingAction.value = LotAction.Finalitzar
                }
            )
        }
    }
}

@Composable
private fun EstatLotFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                maxLines = 1
            )
        }
    )
}

@Composable
private fun LotMobileCard(
    lot: MobileLotDto,
    onIniciar: () -> Unit,
    onFinalitzar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = lot.codiLot.orEmpty().ifBlank { "-" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(
                    R.string.lots_mobile_material,
                    lot.materiaPrimaNom.orEmpty().ifBlank { "-" }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    R.string.lots_mobile_supplier,
                    lot.proveidorNom.orEmpty().ifBlank { "-" }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    R.string.lots_mobile_quantity,
                    lot.quantitat?.toString().orEmpty().ifBlank { "-" }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = traduirEstatLot(lot.estat),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            lot.dataRecepcio?.takeIf { it.isNotBlank() }?.let { dataRecepcio ->
                Text(
                    text = stringResource(R.string.lots_mobile_reception_date, dataRecepcio),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (lot.estat) {
                    "EN_ESTOC" -> {
                        Button(
                            onClick = onIniciar,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.lots_mobile_start_button))
                        }
                    }

                    "OBERT" -> {
                        Button(
                            onClick = onFinalitzar,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.lots_mobile_finish_button))
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(R.string.lots_mobile_no_actions),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun traduirEstatLot(estat: String?): String {
    return when (estat) {
        "EN_ESTOC" -> stringResource(R.string.lots_mobile_status_stock)
        "OBERT" -> stringResource(R.string.lots_mobile_status_open)
        "ACABAT" -> stringResource(R.string.lots_mobile_status_finished)
        else -> stringResource(R.string.lots_mobile_status_unknown)
    }
}