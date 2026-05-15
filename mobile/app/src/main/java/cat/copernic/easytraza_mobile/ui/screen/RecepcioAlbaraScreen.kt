package cat.copernic.easytraza_mobile.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.ui.viewmodel.EditableLotUi
import cat.copernic.easytraza_mobile.ui.viewmodel.RecepcioAlbaraViewModel
import java.io.File

@Composable
fun RecepcioAlbaraScreen(
    currentUserId: Long?,
    currentUserName: String,
    viewModel: RecepcioAlbaraViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val mode = remember { mutableStateOf(RecepcioMode.Manual) }

    val dataRecepcio by viewModel.dataRecepcio.collectAsState()
    val proveidorCif by viewModel.proveidorCif.collectAsState()
    val proveidorNom by viewModel.proveidorNom.collectAsState()
    val crearProveidorSiNoExisteix by viewModel.crearProveidorSiNoExisteix.collectAsState()
    val textOcr by viewModel.textOcr.collectAsState()
    val status by viewModel.status.collectAsState()
    val lotsEditables by viewModel.lotsEditables.collectAsState()
    val saveCompleted by viewModel.saveCompleted.collectAsState()

    LaunchedEffect(currentUserId) {
        viewModel.onUsuariReceptorSeleccionat(currentUserId)
    }

    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            viewModel.marcarSaveConsumit()
            onSaveSuccess()
        }
    }

    val cameraPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val cameraPhotoName = remember { mutableStateOf("") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { captured: Boolean ->
        val uri = cameraPhotoUri.value
        val fileName = cameraPhotoName.value

        if (captured && uri != null && fileName.isNotBlank()) {
            mode.value = RecepcioMode.Ocr
            viewModel.analitzarUri(
                context.contentResolver,
                uri,
                fileName
            )
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            val cameraFile = crearFitxerFotoOcr(context)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cameraFile
            )

            cameraPhotoUri.value = uri
            cameraPhotoName.value = cameraFile.name
            takePictureLauncher.launch(uri)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            mode.value = RecepcioMode.Ocr
            viewModel.analitzarUri(
                context.contentResolver,
                uri,
                "imatge-ocr.jpg"
            )
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            mode.value = RecepcioMode.Ocr
            viewModel.analitzarUri(
                context.contentResolver,
                uri,
                "document-ocr.pdf"
            )
        }
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
                    text = "📋",
                    style = MaterialTheme.typography.displaySmall
                )

                Text(
                    text = stringResource(R.string.recepcio_screen_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = stringResource(R.string.recepcio_screen_subtitle, currentUserName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = mode.value == RecepcioMode.Manual,
                onClick = {
                    mode.value = RecepcioMode.Manual
                    viewModel.prepararModeManual()
                },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text(stringResource(R.string.recepcio_mode_manual))
            }

            SegmentedButton(
                selected = mode.value == RecepcioMode.Ocr,
                onClick = { mode.value = RecepcioMode.Ocr },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text(stringResource(R.string.recepcio_mode_ocr))
            }
        }

        if (mode.value == RecepcioMode.Ocr) {
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
                        text = stringResource(R.string.recepcio_ocr_block_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.recepcio_ocr_block_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.recepcio_ocr_camera_button))
                        }

                        OutlinedButton(
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(stringResource(R.string.recepcio_ocr_image_button))
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            pdfPickerLauncher.launch(arrayOf("application/pdf"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.recepcio_ocr_pdf_button))
                    }

                    if (status.isNotBlank()) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (textOcr.isNotBlank()) {
                        OcrDetectedTextCard(textOcr = textOcr)
                    }
                }
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
                    text = stringResource(R.string.recepcio_form_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = dataRecepcio,
                    onValueChange = viewModel::onDataRecepcioChange,
                    label = { Text(stringResource(R.string.recepcio_field_date)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = proveidorCif,
                    onValueChange = viewModel::onProveidorCifChange,
                    label = { Text(stringResource(R.string.recepcio_field_supplier_cif)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = proveidorNom,
                    onValueChange = viewModel::onProveidorNomChange,
                    label = { Text(stringResource(R.string.recepcio_field_supplier_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OptionSwitchRow(
                    title = stringResource(R.string.recepcio_create_supplier_option),
                    subtitle = stringResource(R.string.recepcio_create_supplier_help),
                    checked = crearProveidorSiNoExisteix,
                    onCheckedChange = viewModel::onCrearProveidorSiNoExisteixChange
                )

                Text(
                    text = stringResource(R.string.recepcio_lots_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                lotsEditables.forEachIndexed { index, lot ->
                    LotEditableCard(
                        index = index,
                        lot = lot,
                        canDelete = lotsEditables.size > 1,
                        onCodiChange = { viewModel.onLotCodiChange(index, it) },
                        onQuantitatChange = { viewModel.onLotQuantitatChange(index, it) },
                        onMateriaChange = { viewModel.onLotMateriaPrimaChange(index, it) },
                        onCrearMateriaChange = { viewModel.onLotCrearMateriaPrimaChange(index, it) },
                        onDelete = { viewModel.eliminarLot(index) }
                    )
                }

                if (mode.value == RecepcioMode.Manual) {
                    OutlinedButton(
                        onClick = { viewModel.afegirLotBuit() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.recepcio_add_lot_button))
                    }
                }

                Button(
                    onClick = { viewModel.guardarAlbara() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.recepcio_save_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


private fun crearFitxerFotoOcr(context: Context): File {
    val directori = File(context.cacheDir, "ocr-camera").apply { mkdirs() }
    return File(directori, "ocr_camera_${System.currentTimeMillis()}.jpg")
}

@Composable
private fun LotEditableCard(
    index: Int,
    lot: EditableLotUi,
    canDelete: Boolean,
    onCodiChange: (String) -> Unit,
    onQuantitatChange: (String) -> Unit,
    onMateriaChange: (String) -> Unit,
    onCrearMateriaChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.recepcio_lot_title, index + 1),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (canDelete) {
                    OutlinedButton(
                        onClick = onDelete,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.recepcio_delete_lot_button),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            OutlinedTextField(
                value = lot.codiLot,
                onValueChange = onCodiChange,
                label = { Text(stringResource(R.string.recepcio_field_lot)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = lot.quantitat,
                onValueChange = onQuantitatChange,
                label = { Text(stringResource(R.string.recepcio_field_quantity)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = lot.materiaPrimaNom,
                onValueChange = onMateriaChange,
                label = { Text(stringResource(R.string.recepcio_field_material)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OptionSwitchRow(
                title = stringResource(R.string.recepcio_create_material_option),
                subtitle = stringResource(R.string.recepcio_create_material_help),
                checked = lot.crearMateriaPrimaSiNoExisteix,
                onCheckedChange = onCrearMateriaChange
            )
        }
    }
}

@Composable
private fun OptionSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun OcrDetectedTextCard(textOcr: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.recepcio_ocr_text_label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            SelectionContainer {
                Text(
                    text = textOcr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private enum class RecepcioMode {
    Manual,
    Ocr
}