package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.domain.usecase.config.GetServerIpUseCase
import cat.copernic.easytraza_mobile.domain.usecase.ocr.AnalitzarAlbaraUseCase
import cat.copernic.easytraza_mobile.domain.usecase.ocr.GuardarAlbaraUseCase
import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.MobileLotSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla o component d’interfície `EditableLotUi` de l'aplicació mobile d'EasyTraza.
 */
data class EditableLotUi(
    val codiLot: String = "",
    val codiMateriaPrimaOcr: String = "",
    val quantitat: String = "",
    val materiaPrimaNom: String = "",
    val crearMateriaPrimaSiNoExisteix: Boolean = false
)

/**
 * Pantalla o component d’interfície `RecepcioAlbaraViewModel` de l'aplicació mobile d'EasyTraza.
 */
class RecepcioAlbaraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IpPreferencesRepository(application.applicationContext)
    private val getServerIpUseCase = GetServerIpUseCase(repository)
    private val analitzarAlbaraUseCase = AnalitzarAlbaraUseCase()
    private val guardarAlbaraUseCase = GuardarAlbaraUseCase()

    private val _dataRecepcio = MutableStateFlow("")
    val dataRecepcio: StateFlow<String> = _dataRecepcio

    private val _proveidorCif = MutableStateFlow("")
    val proveidorCif: StateFlow<String> = _proveidorCif

    private val _proveidorNom = MutableStateFlow("")
    val proveidorNom: StateFlow<String> = _proveidorNom

    private val _crearProveidorSiNoExisteix = MutableStateFlow(false)
    val crearProveidorSiNoExisteix: StateFlow<Boolean> = _crearProveidorSiNoExisteix

    private val _textOcr = MutableStateFlow("")
    val textOcr: StateFlow<String> = _textOcr

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    private val _lotsEditables = MutableStateFlow(listOf(EditableLotUi()))
    val lotsEditables: StateFlow<List<EditableLotUi>> = _lotsEditables

    private val _saveCompleted = MutableStateFlow(false)
    val saveCompleted: StateFlow<Boolean> = _saveCompleted

    private var documentOcrNomOriginal: String? = null
    private var documentOcrNomGuardat: String? = null
    private var documentOcrContentType: String? = null
    private var documentOcrRuta: String? = null
    private var usuariReceptorId: Long? = null

    /**
     * Executa l'operació `onUsuariReceptorSeleccionat`.
     * @param id paràmetre necessari per a l'operació.
     */
    fun onUsuariReceptorSeleccionat(id: Long?) {
        usuariReceptorId = id
    }

    /**
     * Executa l'operació `onDataRecepcioChange`.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onDataRecepcioChange(value: String) {
        _dataRecepcio.value = value
    }

    /**
     * Executa l'operació `onProveidorCifChange`.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onProveidorCifChange(value: String) {
        _proveidorCif.value = value
    }

    /**
     * Executa l'operació `onProveidorNomChange`.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onProveidorNomChange(value: String) {
        _proveidorNom.value = netejarNomProveidorDetectat(value)
    }

    /**
     * Executa l'operació `onCrearProveidorSiNoExisteixChange`.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onCrearProveidorSiNoExisteixChange(value: Boolean) {
        _crearProveidorSiNoExisteix.value = value
    }

    /**
     * Executa l'operació `onLotCodiChange`.
     * @param index paràmetre necessari per a l'operació.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onLotCodiChange(index: Int, value: String) {
        updateLot(index) { it.copy(codiLot = value) }
    }

    /**
     * Executa l'operació `onLotQuantitatChange`.
     * @param index paràmetre necessari per a l'operació.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onLotQuantitatChange(index: Int, value: String) {
        updateLot(index) { it.copy(quantitat = value) }
    }

    /**
     * Executa l'operació `onLotMateriaPrimaChange`.
     * @param index paràmetre necessari per a l'operació.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onLotMateriaPrimaChange(index: Int, value: String) {
        updateLot(index) { it.copy(materiaPrimaNom = value) }
    }

    /**
     * Executa l'operació `onLotCrearMateriaPrimaChange`.
     * @param index paràmetre necessari per a l'operació.
     * @param value paràmetre necessari per a l'operació.
     */
    fun onLotCrearMateriaPrimaChange(index: Int, value: Boolean) {
        updateLot(index) { it.copy(crearMateriaPrimaSiNoExisteix = value) }
    }

    /**
     * Executa l'operació `afegirLotBuit`.
     */
    fun afegirLotBuit() {
        _lotsEditables.value += EditableLotUi()
    }

    /**
     * Executa l'operació `eliminarLot`.
     * @param index paràmetre necessari per a l'operació.
     */
    fun eliminarLot(index: Int) {
        val actuals = _lotsEditables.value.toMutableList()

        if (actuals.size <= 1) {
            actuals[0] = EditableLotUi()
        } else if (index in actuals.indices) {
            actuals.removeAt(index)
        }

        _lotsEditables.value = actuals
    }

    /**
     * Executa l'operació `marcarSaveConsumit`.
     */
    fun marcarSaveConsumit() {
        _saveCompleted.value = false
    }

    /**
     * Executa l'operació `prepararModeManual`.
     */
    fun prepararModeManual() {
        _textOcr.value = ""
        _status.value = ""
        _dataRecepcio.value = todayIso()

        if (_lotsEditables.value.isEmpty()) {
            _lotsEditables.value = listOf(EditableLotUi())
        }
    }

    /**
     * Executa l'operació `analitzarUri`.
     * @param contentResolver paràmetre necessari per a l'operació.
     * @param uri paràmetre necessari per a l'operació.
     * @param fileName paràmetre necessari per a l'operació.
     */
    fun analitzarUri(contentResolver: ContentResolver, uri: Uri, fileName: String) {
        viewModelScope.launch {
            try {
                _saveCompleted.value = false
                _status.value = getApplication<Application>().getString(R.string.ocr_processing_document)

                val savedIp = getServerIpUseCase()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val mimeType = contentResolver.getType(uri) ?: inferMimeTypeFromName(fileName)

                val extension = when {
                    mimeType.contains("pdf", ignoreCase = true) -> ".pdf"
                    mimeType.contains("png", ignoreCase = true) -> ".png"
                    else -> ".jpg"
                }

                val tempFile = File(
                    getApplication<Application>().cacheDir,
                    "ocr_input_${System.currentTimeMillis()}$extension"
                )

                val copied = copyUriToFile(contentResolver, uri, tempFile)

                if (!copied || !tempFile.exists() || tempFile.length() <= 0L) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_file)
                    return@launch
                }

                val fitxerPreparat = prepararFitxerPerOcr(tempFile, mimeType)

                val requestBody = fitxerPreparat.file.asRequestBody(
                    fitxerPreparat.mimeType.toMediaTypeOrNull()
                )
                val part = MultipartBody.Part.createFormData(
                    "fitxer",
                    fitxerPreparat.file.name,
                    requestBody
                )

                val resposta = analitzarAlbaraUseCase(savedIp, part)

                omplirDesDeResposta(resposta)

                _status.value = getApplication<Application>().getString(R.string.ocr_success)

            } catch (ex: Exception) {
                _status.value = NetworkErrorMapper.ocrError(
                    getApplication(),
                    ex
                )
            }
        }
    }

    /**
     * Executa l'operació `guardarAlbara`.
     */
    fun guardarAlbara() {
        viewModelScope.launch {
            try {
                _saveCompleted.value = false
                _status.value = getApplication<Application>().getString(R.string.ocr_saving)

                val savedIp = getServerIpUseCase()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val request = MobileAlbaraSaveRequestDto(
                    dataRecepcio = _dataRecepcio.value.ifBlank { todayIso() },
                    proveidorCif = _proveidorCif.value.ifBlank { null },
                    proveidorNom = netejarNomProveidorDetectat(_proveidorNom.value)
                        .ifBlank { "Proveïdor OCR" },
                    crearProveidorSiNoExisteix = _crearProveidorSiNoExisteix.value,
                    usuariReceptorId = usuariReceptorId,
                    documentOcrNomOriginal = documentOcrNomOriginal,
                    documentOcrNomGuardat = documentOcrNomGuardat,
                    documentOcrContentType = documentOcrContentType,
                    documentOcrRuta = documentOcrRuta,
                    lots = buildLotsPerGuardar()
                )

                val response = guardarAlbaraUseCase(savedIp, request)

                if (response.isSuccessful) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_saved)
                    _saveCompleted.value = true
                } else {
                    _status.value = getApplication<Application>().getString(R.string.ocr_save_error)
                }

            } catch (ex: Exception) {
                _status.value = NetworkErrorMapper.saveAlbaraError(
                    getApplication(),
                    ex
                )
            }
        }
    }

    /**
     * Executa l'operació `buildLotsPerGuardar`.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun buildLotsPerGuardar(): List<MobileLotSaveRequestDto> {
        return _lotsEditables.value.mapNotNull { lot ->
            val codi = lot.codiLot.trim()
            val materia = lot.materiaPrimaNom.trim()
            val quantitat = lot.quantitat.trim().replace(",", ".").toDoubleOrNull()

            if (codi.isBlank() && materia.isBlank() && quantitat == null) {
                null
            } else {
                MobileLotSaveRequestDto(
                    codiLot = codi,
                    codiMateriaPrimaOcr = lot.codiMateriaPrimaOcr.trim().ifBlank { null },
                    quantitat = quantitat,
                    materiaPrimaNom = materia,
                    crearMateriaPrimaSiNoExisteix = lot.crearMateriaPrimaSiNoExisteix
                )
            }
        }.ifEmpty {
            listOf(
                MobileLotSaveRequestDto(
                    codiLot = "",
                    codiMateriaPrimaOcr = null,
                    quantitat = null,
                    materiaPrimaNom = "",
                    crearMateriaPrimaSiNoExisteix = false
                )
            )
        }
    }

    /**
     * Executa l'operació `omplirDesDeResposta`.
     * @param resposta paràmetre necessari per a l'operació.
     */
    private fun omplirDesDeResposta(resposta: OcrAlbaraResponseDto) {
        _proveidorCif.value = resposta.proveidorCif.orEmpty()
        _dataRecepcio.value = resoldreDataRecepcio(resposta.dataAlbara)
        _textOcr.value = resposta.textDetectat.orEmpty()
        documentOcrNomOriginal = resposta.documentOcrNomOriginal
        documentOcrNomGuardat = resposta.documentOcrNomGuardat
        documentOcrContentType = resposta.documentOcrContentType
        documentOcrRuta = resposta.documentOcrRuta

        val lotsMapejats = resposta.lots.map { lot ->
            EditableLotUi(
                codiLot = lot.codiLot.orEmpty(),
                codiMateriaPrimaOcr = lot.codiMateriaPrimaOcr.orEmpty(),
                quantitat = lot.quantitat?.toString().orEmpty(),
                materiaPrimaNom = lot.materiaPrima.orEmpty(),
                crearMateriaPrimaSiNoExisteix = false
            )
        }

        _lotsEditables.value = lotsMapejats.ifEmpty {
            listOf(EditableLotUi())
        }

        _proveidorNom.value = extraurePossibleNomProveidor(_textOcr.value)
    }

    /**
     * Executa l'operació `resoldreDataRecepcio`.
     * @param dataOcr paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun resoldreDataRecepcio(dataOcr: String?): String {
        val avui = todayIso()
        val dataOcrNormalitzada = dataOcr
            ?.let { normalitzarData(it) }
            ?.takeIf { it.isNotBlank() }
            ?: return avui

        return if (dataOcrNormalitzada <= avui) {
            dataOcrNormalitzada
        } else {
            avui
        }
    }

    /**
     * Executa l'operació `extraurePossibleNomProveidor`.
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun extraurePossibleNomProveidor(text: String): String {
        return text.lineSequence()
            .map { netejarNomProveidorDetectat(it) }
            .filter { it.isNotBlank() }
            .firstOrNull { line ->
                !line.matches(Regex("""[A-Z]\d{7,8}""")) &&
                        !line.matches(Regex("""\d{2}/\d{2}/\d{4}""")) &&
                        !line.matches(Regex("""\d{4}-\d{2}-\d{2}""")) &&
                        !line.matches(
                            Regex(
                                """.*\d+\s?(KG|G|L|ML|UD|UDS).*""",
                                RegexOption.IGNORE_CASE
                            )
                        )
            }
            .orEmpty()
    }

    /**
     * Executa l'operació `netejarNomProveidorDetectat`.
     * @param text paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun netejarNomProveidorDetectat(text: String?): String {
        if (text.isNullOrBlank()) {
            return ""
        }

        return text.trim()
            .replace(Regex("""(?i)^PROVEIDOR\s+DETECTAT\s+OCR\s*:\s*"""), "")
            .replace(Regex("""(?i)^PROVEEDOR\s+DETECTADO\s+POR\s+OCR\s*:\s*"""), "")
            .replace(Regex("""(?i)^PROVEÏDOR\s+DETECTAT\s+PER\s+OCR\s*:\s*"""), "")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
    }

    /**
     * Executa l'operació `normalitzarData`.
     * @param value paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun normalitzarData(value: String): String {
        val clean = value.trim().replace("/", "-")
        val parts = clean.split("-")

        return when (parts.size) {
            3 if parts[0].length == 2 -> "${parts[2]}-${parts[1]}-${parts[0]}"
            3 if parts[0].length == 4 -> clean
            else -> ""
        }
    }

    private data class FitxerOcrPreparat(
        val file: File,
        val mimeType: String
    )

    /**
     * Executa l'operació `prepararFitxerPerOcr`.
     * @param file paràmetre necessari per a l'operació.
     * @param mimeType paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun prepararFitxerPerOcr(file: File, mimeType: String): FitxerOcrPreparat {
        if (!mimeType.startsWith("image/", ignoreCase = true)) {
            return FitxerOcrPreparat(file, mimeType)
        }

        val orientacio = llegirOrientacioExif(file)
        if (orientacio == ExifInterface.ORIENTATION_NORMAL ||
            orientacio == ExifInterface.ORIENTATION_UNDEFINED
        ) {
            return FitxerOcrPreparat(file, mimeType)
        }

        val bitmapOriginal = BitmapFactory.decodeFile(file.absolutePath)
            ?: return FitxerOcrPreparat(file, mimeType)

        val bitmapNormalitzat = aplicarOrientacioExif(bitmapOriginal, orientacio)
        if (bitmapNormalitzat === bitmapOriginal) {
            return FitxerOcrPreparat(file, mimeType)
        }

        val fitxerNormalitzat = File(
            getApplication<Application>().cacheDir,
            "ocr_input_normalitzat_${System.currentTimeMillis()}.jpg"
        )

        return try {
            FileOutputStream(fitxerNormalitzat).use { output ->
                bitmapNormalitzat.compress(Bitmap.CompressFormat.JPEG, 100, output)
                output.flush()
            }

            FitxerOcrPreparat(fitxerNormalitzat, "image/jpeg")
        } catch (_: Exception) {
            FitxerOcrPreparat(file, mimeType)
        } finally {
            bitmapNormalitzat.recycle()
            bitmapOriginal.recycle()
        }
    }

    /**
     * Executa l'operació `llegirOrientacioExif`.
     * @param file paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun llegirOrientacioExif(file: File): Int {
        return try {
            ExifInterface(file.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Executa l'operació `aplicarOrientacioExif`.
     * @param bitmap paràmetre necessari per a l'operació.
     * @param orientacio paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun aplicarOrientacioExif(bitmap: Bitmap, orientacio: Int): Bitmap {
        val matrix = Matrix()

        when (orientacio) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(-90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Executa l'operació `copyUriToFile`.
     * @param contentResolver paràmetre necessari per a l'operació.
     * @param uri paràmetre necessari per a l'operació.
     * @param file paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun copyUriToFile(contentResolver: ContentResolver, uri: Uri, file: File): Boolean {
        return try {
            contentResolver.openInputStream(uri).use { input ->
                if (input == null) {
                    return false
                }

                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            true

        } catch (_: Exception) {
            false
        }
    }

    /**
     * Executa l'operació `inferMimeTypeFromName`.
     * @param fileName paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun inferMimeTypeFromName(fileName: String): String {
        val lower = fileName.lowercase(Locale.ROOT)

        return when {
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".png") -> "image/png"
            else -> "image/jpeg"
        }
    }

    /**
     * Executa l'operació `todayIso`.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun todayIso(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Executa l'operació `updateLot`.
     * @param index paràmetre necessari per a l'operació.
     * @param transform paràmetre necessari per a l'operació.
     */
    private fun updateLot(index: Int, transform: (EditableLotUi) -> EditableLotUi) {
        val actuals = _lotsEditables.value.toMutableList()

        if (index in actuals.indices) {
            actuals[index] = transform(actuals[index])
            _lotsEditables.value = actuals
        }
    }
}
