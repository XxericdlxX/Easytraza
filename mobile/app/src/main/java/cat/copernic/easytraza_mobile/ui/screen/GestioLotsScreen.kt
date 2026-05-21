package cat.copernic.easytraza_mobile.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import cat.copernic.easytraza_mobile.ui.viewmodel.GestioLotsViewModel
import java.util.Calendar
import java.util.Locale

/**
 * Accions disponibles sobre un lot des del llistat mobile.
 */
private enum class LotAction {
    Iniciar,
    Finalitzar
}

/**
 * Pantalla de gestió de lots del client mobile.
 */
@Composable
fun GestioLotsScreen(
    viewModel: GestioLotsViewModel,
    onBack: () -> Unit
) {
    val lots by viewModel.lots.collectAsState()
    val status by viewModel.status.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val codisLotDisponibles by viewModel.codisLotDisponibles.collectAsState()
    val materiesDisponibles by viewModel.materiesDisponibles.collectAsState()
    val filtreCodi by viewModel.filtreCodi.collectAsState()
    val filtreMateria by viewModel.filtreMateria.collectAsState()
    val filtreDataRecepcio by viewModel.filtreDataRecepcio.collectAsState()
    val filtreEstat by viewModel.filtreEstat.collectAsState()
    val ordreCamp by viewModel.ordreCamp.collectAsState()
    val ordreDireccio by viewModel.ordreDireccio.collectAsState()

    val pendingLot = remember { mutableStateOf<MobileLotDto?>(null) }
    val pendingAction = remember { mutableStateOf<LotAction?>(null) }
    var filtresOberts by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendari = remember { Calendar.getInstance() }

    fun obrirSelectorData() {
        DatePickerDialog(
            context,
            { _, any, mes, dia ->
                viewModel.actualitzarFiltreDataRecepcio(
                    String.format(Locale.ROOT, "%04d-%02d-%02d", any, mes + 1, dia)
                )
            },
            calendari.get(Calendar.YEAR),
            calendari.get(Calendar.MONTH),
            calendari.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(Unit) {
        viewModel.carregarLots()
    }

    ConfirmacioAccioLotDialog(
        lot = pendingLot.value,
        action = pendingAction.value,
        onDismiss = {
            pendingLot.value = null
            pendingAction.value = null
        },
        onConfirm = { lot, action ->
            when (action) {
                LotAction.Iniciar -> viewModel.iniciarLot(lot.id)
                LotAction.Finalitzar -> viewModel.finalitzarLot(lot.id)
            }
            pendingLot.value = null
            pendingAction.value = null
        }
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "🔁", style = MaterialTheme.typography.displaySmall)

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

        FiltresIOrdenacioCard(
            expanded = filtresOberts,
            onToggle = { filtresOberts = !filtresOberts },
            codisLotDisponibles = codisLotDisponibles,
            materiesDisponibles = materiesDisponibles,
            filtreCodi = filtreCodi,
            filtreMateria = filtreMateria,
            filtreDataRecepcio = filtreDataRecepcio,
            filtreEstat = filtreEstat,
            ordreCamp = ordreCamp,
            ordreDireccio = ordreDireccio,
            onFiltreCodi = viewModel::actualitzarFiltreCodi,
            onFiltreMateria = viewModel::actualitzarFiltreMateria,
            onFiltreData = viewModel::actualitzarFiltreDataRecepcio,
            onFiltreEstat = viewModel::actualitzarFiltreEstat,
            onOrdreCamp = viewModel::actualitzarOrdreCamp,
            onOrdreDireccio = viewModel::actualitzarOrdreDireccio,
            onClear = viewModel::netejarFiltres,
            onOpenDate = ::obrirSelectorData
        )

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = stringResource(R.string.lots_mobile_empty_filtered),
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

/**
 * Diàleg de confirmació per iniciar o finalitzar un lot.
 */
@Composable
private fun ConfirmacioAccioLotDialog(
    lot: MobileLotDto?,
    action: LotAction?,
    onDismiss: () -> Unit,
    onConfirm: (MobileLotDto, LotAction) -> Unit
) {
    if (lot == null || action == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
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
            Button(onClick = { onConfirm(lot, action) }) {
                Text(stringResource(R.string.lots_mobile_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.lots_mobile_cancel))
            }
        }
    )
}

/**
 * Targeta plegable amb filtres i ordenació de lots.
 */
@Composable
private fun FiltresIOrdenacioCard(
    expanded: Boolean,
    onToggle: () -> Unit,
    codisLotDisponibles: List<String>,
    materiesDisponibles: List<String>,
    filtreCodi: String,
    filtreMateria: String,
    filtreDataRecepcio: String,
    filtreEstat: String,
    ordreCamp: String,
    ordreDireccio: String,
    onFiltreCodi: (String) -> Unit,
    onFiltreMateria: (String) -> Unit,
    onFiltreData: (String) -> Unit,
    onFiltreEstat: (String) -> Unit,
    onOrdreCamp: (String) -> Unit,
    onOrdreDireccio: (String) -> Unit,
    onClear: () -> Unit,
    onOpenDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.lots_mobile_filters_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.lots_mobile_filters_summary),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onToggle) {
                    Text(
                        text = if (expanded) {
                            stringResource(R.string.lots_mobile_filters_hide)
                        } else {
                            stringResource(R.string.lots_mobile_filters_show)
                        }
                    )
                }
            }

            if (expanded) {
                FiltreDropdown(
                    label = stringResource(R.string.lots_mobile_filter_code),
                    selectedValue = filtreCodi,
                    emptyLabel = stringResource(R.string.lots_mobile_filter_code_all),
                    options = codisLotDisponibles,
                    onSelected = onFiltreCodi
                )

                FiltreDropdown(
                    label = stringResource(R.string.lots_mobile_filter_material),
                    selectedValue = filtreMateria,
                    emptyLabel = stringResource(R.string.lots_mobile_filter_material_all),
                    options = materiesDisponibles,
                    onSelected = onFiltreMateria
                )

                OutlinedTextField(
                    value = filtreDataRecepcio,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.lots_mobile_filter_date)) },
                    supportingText = { Text(stringResource(R.string.lots_mobile_filter_date_help)) },
                    trailingIcon = {
                        IconButton(onClick = onOpenDate) {
                            Text("📅")
                        }
                    }
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
                            onClick = { onFiltreEstat("") },
                            modifier = Modifier.weight(1f)
                        )
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_status_stock),
                            selected = filtreEstat == "EN_ESTOC",
                            onClick = { onFiltreEstat("EN_ESTOC") },
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
                            onClick = { onFiltreEstat("OBERT") },
                            modifier = Modifier.weight(1f)
                        )
                        EstatLotFilterChip(
                            text = stringResource(R.string.lots_mobile_status_finished),
                            selected = filtreEstat == "ACABAT",
                            onClick = { onFiltreEstat("ACABAT") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.lots_mobile_sort_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                ValorDropdown(
                    label = stringResource(R.string.lots_mobile_sort_field),
                    selectedValue = ordreCamp,
                    options = ordreCampOptions(),
                    onSelected = onOrdreCamp
                )

                ValorDropdown(
                    label = stringResource(R.string.lots_mobile_sort_direction),
                    selectedValue = ordreDireccio,
                    options = ordreDireccioOptions(),
                    onSelected = onOrdreDireccio
                )

                TextButton(
                    onClick = onClear,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.lots_mobile_filter_clear))
                }
            }
        }
    }
}

/**
 * Desplegable per filtrar amb opció buida.
 */
@Composable
private fun FiltreDropdown(
    label: String,
    selectedValue: String,
    emptyLabel: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedValue.ifBlank { emptyLabel },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Text("⌄")
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text(emptyLabel) },
                onClick = {
                    onSelected("")
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Desplegable per seleccionar una opció amb codi intern i etiqueta visible.
 */
@Composable
private fun ValorDropdown(
    label: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second.orEmpty()

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Text("⌄")
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        onSelected(option.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Opcions de camp d'ordenació.
 */
@Composable
private fun ordreCampOptions(): List<Pair<String, String>> {
    return listOf(
        GestioLotsViewModel.ORDRE_CODI_LOT to stringResource(R.string.lots_mobile_sort_code),
        GestioLotsViewModel.ORDRE_MATERIA to stringResource(R.string.lots_mobile_sort_material),
        GestioLotsViewModel.ORDRE_PROVEIDOR to stringResource(R.string.lots_mobile_sort_supplier),
        GestioLotsViewModel.ORDRE_QUANTITAT to stringResource(R.string.lots_mobile_sort_quantity),
        GestioLotsViewModel.ORDRE_DATA_RECEPCIO to stringResource(R.string.lots_mobile_sort_reception_date),
        GestioLotsViewModel.ORDRE_ESTAT to stringResource(R.string.lots_mobile_sort_status)
    )
}

/**
 * Opcions de direcció d'ordenació.
 */
@Composable
private fun ordreDireccioOptions(): List<Pair<String, String>> {
    return listOf(
        GestioLotsViewModel.ORDRE_ASC to stringResource(R.string.lots_mobile_sort_asc),
        GestioLotsViewModel.ORDRE_DESC to stringResource(R.string.lots_mobile_sort_desc)
    )
}

/**
 * Chip d'estat del lot.
 */
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
        label = { Text(text = text, maxLines = 1) }
    )
}

/**
 * Targeta resum d'un lot.
 */
@Composable
private fun LotMobileCard(
    lot: MobileLotDto,
    onIniciar: () -> Unit,
    onFinalitzar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                text = stringResource(R.string.lots_mobile_material, lot.materiaPrimaNom.orEmpty().ifBlank { "-" }),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.lots_mobile_supplier, lot.proveidorNom.orEmpty().ifBlank { "-" }),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.lots_mobile_quantity, lot.quantitat?.toString().orEmpty().ifBlank { "-" }),
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


            AccionsLotRow(
                lot = lot,
                onIniciar = onIniciar,
                onFinalitzar = onFinalitzar
            )
        }
    }
}

/**
 * Botons d'acció disponibles segons l'estat del lot.
 */
@Composable
private fun AccionsLotRow(
    lot: MobileLotDto,
    onIniciar: () -> Unit,
    onFinalitzar: () -> Unit
) {
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

/**
 * Tradueix el codi d'estat del backend a text visible.
 */
@Composable
private fun traduirEstatLot(estat: String?): String {
    return when (estat) {
        "EN_ESTOC" -> stringResource(R.string.lots_mobile_status_stock)
        "OBERT" -> stringResource(R.string.lots_mobile_status_open)
        "ACABAT" -> stringResource(R.string.lots_mobile_status_finished)
        else -> stringResource(R.string.lots_mobile_status_unknown)
    }
}
