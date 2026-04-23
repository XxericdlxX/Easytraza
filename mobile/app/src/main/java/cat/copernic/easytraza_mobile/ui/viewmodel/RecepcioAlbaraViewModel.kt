package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.RetrofitClient
import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.MobileLotSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

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

    private val _status = MutableStateFlow("Preparat")
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
                _status.value = "Analitzant document..."

                val tempFile = File(getApplication<Application>().cacheDir, fileName)
                contentResolver.openInputStream(uri).use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input?.copyTo(output)
                    }
                }

                val baseUrl = obtenirBaseUrl()
                val api = RetrofitClient.create(baseUrl)

                val part = MultipartBody.Part.createFormData(
                    "fitxer",
                    tempFile.name,
                    tempFile.asRequestBody()
                )

                val resposta = api.analitzarAlbara(part)
                omplirDesDeResposta(resposta)

                _status.value = "OCR completat correctament"
            } catch (_: Exception) {
                _status.value = "Error analitzant el document"
            }
        }
    }

    fun analitzarBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _status.value = "Analitzant fotografia..."

                val tempFile = File(getApplication<Application>().cacheDir, "captura-ocr.jpg")
                FileOutputStream(tempFile).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
                }

                val baseUrl = obtenirBaseUrl()
                val api = RetrofitClient.create(baseUrl)

                val part = MultipartBody.Part.createFormData(
                    "fitxer",
                    tempFile.name,
                    tempFile.asRequestBody()
                )

                val resposta = api.analitzarAlbara(part)
                omplirDesDeResposta(resposta)

                _status.value = "OCR completat correctament"
            } catch (_: Exception) {
                _status.value = "Error analitzant la fotografia"
            }
        }
    }

    fun guardarAlbara() {
        viewModelScope.launch {
            try {
                _status.value = "Desant albarà..."

                val baseUrl = obtenirBaseUrl()
                val api = RetrofitClient.create(baseUrl)

                val request = MobileAlbaraSaveRequestDto(
                    dataRecepcio = _dataRecepcio.value,
                    proveidorCif = _proveidorCif.value.ifBlank { null },
                    proveidorNom = _proveidorNom.value,
                    lots = listOf(
                        MobileLotSaveRequestDto(
                            codiLot = _codiLot.value,
                            quantitat = _quantitat.value.toIntOrNull(),
                            materiaPrimaNom = _materiaPrima.value
                        )
                    )
                )

                val response = api.guardarAlbara(request)
                _status.value = if (response.isSuccessful) {
                    "Albarà desat correctament"
                } else {
                    "Error desant l'albarà"
                }
            } catch (_: Exception) {
                _status.value = "Error desant l'albarà"
            }
        }
    }

    private suspend fun obtenirBaseUrl(): String {
        val savedIp = repository.serverIpFlow.first()
        return RetrofitClient.buildBaseUrl(savedIp)
    }

    private fun omplirDesDeResposta(resposta: OcrAlbaraResponseDto) {
        _proveidorCif.value = resposta.proveidorCif.orEmpty()

        _dataRecepcio.value = resposta.dataAlbara
            ?.replace("/", "-")
            ?.let { normalitzarDataSiCal(it) }
            ?: _dataRecepcio.value

        _textOcr.value = resposta.textDetectat.orEmpty()

        val primerLot = resposta.lots.firstOrNull()
        _codiLot.value = primerLot?.codiLot.orEmpty()
        _quantitat.value = primerLot?.quantitat?.toInt()?.toString().orEmpty()
        _materiaPrima.value = primerLot?.materiaPrima.orEmpty()

        if (_proveidorNom.value.isBlank() && _textOcr.value.isNotBlank()) {
            _proveidorNom.value = _textOcr.value
                .lineSequence()
                .map { it.trim() }
                .firstOrNull {
                    it.isNotBlank() &&
                            !it.contains(Regex("\\d{2}/\\d{2}/\\d{4}")) &&
                            !it.contains(Regex("\\d{4}-\\d{2}-\\d{2}"))
                }
                .orEmpty()
        }
    }

    private fun normalitzarDataSiCal(valor: String): String {
        val partsBarra = valor.split("-")
        return if (partsBarra.size == 3 && partsBarra[0].length == 2) {
            "${partsBarra[2]}-${partsBarra[1]}-${partsBarra[0]}"
        } else {
            valor
        }
    }
}