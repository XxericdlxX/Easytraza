package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.RetrofitClient
import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.MobileLotSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecepcioAlbaraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IpPreferencesRepository(application.applicationContext)

    private val _dataRecepcio = MutableStateFlow("")
    val dataRecepcio: StateFlow<String> = _dataRecepcio

    private val _proveidorCif = MutableStateFlow("")
    val proveidorCif: StateFlow<String> = _proveidorCif

    private val _proveidorNom = MutableStateFlow("")
    val proveidorNom: StateFlow<String> = _proveidorNom

    private val _codiLot = MutableStateFlow("")
    val codiLot: StateFlow<String> = _codiLot

    private val _quantitat = MutableStateFlow("")
    val quantitat: StateFlow<String> = _quantitat

    private val _materiaPrima = MutableStateFlow("")
    val materiaPrima: StateFlow<String> = _materiaPrima

    private val _textOcr = MutableStateFlow("")
    val textOcr: StateFlow<String> = _textOcr

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    fun onDataRecepcioChange(value: String) { _dataRecepcio.value = value }
    fun onProveidorCifChange(value: String) { _proveidorCif.value = value }
    fun onProveidorNomChange(value: String) { _proveidorNom.value = value }
    fun onCodiLotChange(value: String) { _codiLot.value = value }
    fun onQuantitatChange(value: String) { _quantitat.value = value }
    fun onMateriaPrimaChange(value: String) { _materiaPrima.value = value }

    fun analitzarUri(contentResolver: ContentResolver, uri: Uri, fileName: String) {
        viewModelScope.launch {
            try {
                _status.value = getApplication<Application>().getString(R.string.ocr_processing_document)

                val savedIp = repository.serverIpFlow.first().trim()
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

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))

                val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(
                    "fitxer",
                    tempFile.name,
                    requestBody
                )

                val resposta = api.analitzarAlbara(part)
                omplirDesDeResposta(resposta)

                _status.value = getApplication<Application>().getString(R.string.ocr_success)
            } catch (ex: Exception) {
                _status.value = ex.message ?: getApplication<Application>().getString(R.string.ocr_processing_error)
            }
        }
    }

    fun analitzarBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _status.value = getApplication<Application>().getString(R.string.ocr_processing_photo)

                val savedIp = repository.serverIpFlow.first().trim()
                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val tempFile = File(
                    getApplication<Application>().cacheDir,
                    "ocr_camera_${System.currentTimeMillis()}.jpg"
                )

                FileOutputStream(tempFile).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)
                    output.flush()
                }

                if (!tempFile.exists() || tempFile.length() <= 0L) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_file)
                    return@launch
                }

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))

                val requestBody = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData(
                    "fitxer",
                    tempFile.name,
                    requestBody
                )

                val resposta = api.analitzarAlbara(part)
                omplirDesDeResposta(resposta)

                _status.value = getApplication<Application>().getString(R.string.ocr_success)
            } catch (ex: Exception) {
                _status.value = ex.message ?: getApplication<Application>().getString(R.string.ocr_processing_error)
            }
        }
    }

    fun guardarAlbara() {
        viewModelScope.launch {
            try {
                _status.value = getApplication<Application>().getString(R.string.ocr_saving)

                val savedIp = repository.serverIpFlow.first().trim()
                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))

                val request = MobileAlbaraSaveRequestDto(
                    dataRecepcio = _dataRecepcio.value.ifBlank { todayIso() },
                    proveidorCif = _proveidorCif.value.ifBlank { null },
                    proveidorNom = _proveidorNom.value.ifBlank { "Proveïdor OCR" },
                    lots = listOf(
                        MobileLotSaveRequestDto(
                            codiLot = _codiLot.value.trim(),
                            quantitat = _quantitat.value.trim().toIntOrNull(),
                            materiaPrimaNom = _materiaPrima.value.trim()
                        )
                    )
                )

                val response = api.guardarAlbara(request)
                _status.value = if (response.isSuccessful) {
                    getApplication<Application>().getString(R.string.ocr_saved)
                } else {
                    getApplication<Application>().getString(R.string.ocr_save_error)
                }
            } catch (_: Exception) {
                _status.value = getApplication<Application>().getString(R.string.ocr_save_error)
            }
        }
    }

    private fun omplirDesDeResposta(resposta: OcrAlbaraResponseDto) {
        _proveidorCif.value = resposta.proveidorCif.orEmpty()

        val normalizedDate = resposta.dataAlbara?.let { normalitzarData(it) }.orEmpty()
        if (normalizedDate.isNotBlank()) {
            _dataRecepcio.value = normalizedDate
        }

        _textOcr.value = resposta.textDetectat.orEmpty()

        val primerLot = resposta.lots.firstOrNull()
        _codiLot.value = primerLot?.codiLot.orEmpty()
        _quantitat.value = primerLot?.quantitat?.toInt()?.toString().orEmpty()
        _materiaPrima.value = primerLot?.materiaPrima.orEmpty()

        if (_proveidorNom.value.isBlank()) {
            _proveidorNom.value = extraurePossibleNomProveidor(_textOcr.value)
        }
    }

    private fun extraurePossibleNomProveidor(text: String): String {
        return text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .firstOrNull { line ->
                !line.matches(Regex("""[A-Z]\d{7,8}""")) &&
                        !line.matches(Regex("""\d{2}/\d{2}/\d{4}""")) &&
                        !line.matches(Regex("""\d{4}-\d{2}-\d{2}""")) &&
                        !line.matches(Regex(""".*\d+\s?(KG|G|L|ML|UD|UDS).*""", RegexOption.IGNORE_CASE))
            }
            .orEmpty()
    }

    private fun normalitzarData(value: String): String {
        val clean = value.trim().replace("/", "-")
        val parts = clean.split("-")
        return if (parts.size == 3 && parts[0].length == 2) {
            "${parts[2]}-${parts[1]}-${parts[0]}"
        } else {
            clean
        }
    }

    private fun copyUriToFile(contentResolver: ContentResolver, uri: Uri, file: File): Boolean {
        return try {
            contentResolver.openInputStream(uri).use { input ->
                if (input == null) return false
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

    private fun inferMimeTypeFromName(fileName: String): String {
        val lower = fileName.lowercase(Locale.ROOT)
        return when {
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".png") -> "image/png"
            else -> "image/jpeg"
        }
    }

    private fun todayIso(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}