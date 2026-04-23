package cat.copernic.easytraza_mobile.ui.screen

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.network.dto.MobileLotSaveRequestDto
import cat.copernic.easytraza_mobile.ui.viewmodel.RecepcioAlbaraViewModel

@Composable
fun RecepcioAlbaraScreen(
    currentUserName: String,
    viewModel: RecepcioAlbaraViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val mode = remember { mutableStateOf(RecepcioMode.Manual) }

    val dataRecepcio by viewModel.dataRecepcio.collectAsState()
    val proveidorCif by viewModel.proveidorCif.collectAsState()
    val proveidorNom by viewModel.proveidorNom.collectAsState()
    val codiLot by viewModel.codiLot.collectAsState()
    val quantitat by viewModel.quantitat.collectAsState()
    val materiaPrima by viewModel.materiaPrima.collectAsState()
    val textOcr by viewModel.textOcr.collectAsState()
    val status by viewModel.status.collectAsState()
    val lotsDetectats by viewModel.lotsDetectats.collectAsState()

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            mode.value = RecepcioMode.Ocr
            viewModel.analitzarBitmap(bitmap)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            takePictureLauncher.launch(null)
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
                    text = "📦",
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
                onClick = { mode.value = RecepcioMode.Manual },
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
                    OutlinedTextField(
                        value = textOcr,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.recepcio_ocr_text_label)) },
                        readOnly = true,
                        minLines = 6,
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                if (lotsDetectats.isNotEmpty()) {
                    HorizontalDivider()

                    Text(
                        text = "Lotes detectados por OCR",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    lotsDetectats.forEachIndexed { index: Int, lot: MobileLotSaveRequestDto ->
                        LotDetectatCard(
                            index = index,
                            lot = lot,
                            esPrincipal = index == 0
                        )
                    }

                    Text(
                        text = "El primer lote detectado se carga en el formulario editable. Si hay más lotes, también se enviarán al guardar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

                OutlinedTextField(
                    value = codiLot,
                    onValueChange = viewModel::onCodiLotChange,
                    label = { Text(stringResource(R.string.recepcio_field_lot)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = quantitat,
                    onValueChange = viewModel::onQuantitatChange,
                    label = { Text(stringResource(R.string.recepcio_field_quantity)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = materiaPrima,
                    onValueChange = viewModel::onMateriaPrimaChange,
                    label = { Text(stringResource(R.string.recepcio_field_material)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

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

@Composable
private fun LotDetectatCard(
    index: Int,
    lot: MobileLotSaveRequestDto,
    esPrincipal: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esPrincipal) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (esPrincipal) "Lote ${index + 1} (principal)" else "Lote ${index + 1}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (esPrincipal) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            if (lot.codiLot.isNotBlank()) {
                Text(
                    text = "Código: ${lot.codiLot}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (lot.materiaPrimaNom.isNotBlank()) {
                Text(
                    text = "Materia prima: ${lot.materiaPrimaNom}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (lot.quantitat != null) {
                Text(
                    text = "Cantidad: ${lot.quantitat}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private enum class RecepcioMode {
    Manual,
    Ocr
}