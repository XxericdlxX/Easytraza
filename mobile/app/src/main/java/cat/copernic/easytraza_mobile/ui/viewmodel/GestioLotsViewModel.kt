package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.network.RetrofitClient
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GestioLotsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IpPreferencesRepository(application.applicationContext)

    private val _lotsComplets = MutableStateFlow<List<MobileLotDto>>(emptyList())

    private val _lots = MutableStateFlow<List<MobileLotDto>>(emptyList())
    val lots: StateFlow<List<MobileLotDto>> = _lots

    private val _filtreCodi = MutableStateFlow("")
    val filtreCodi: StateFlow<String> = _filtreCodi

    private val _filtreMateria = MutableStateFlow("")
    val filtreMateria: StateFlow<String> = _filtreMateria

    private val _filtreDataRecepcio = MutableStateFlow("")
    val filtreDataRecepcio: StateFlow<String> = _filtreDataRecepcio

    private val _filtreEstat = MutableStateFlow("")
    val filtreEstat: StateFlow<String> = _filtreEstat

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun carregarLots() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _status.value = ""

                val savedIp = repository.serverIpFlow.first().trim()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))
                _lotsComplets.value = api.llistarLots()
                aplicarFiltres()

                if (_lotsComplets.value.isEmpty()) {
                    _status.value = getApplication<Application>().getString(R.string.lots_mobile_empty)
                }

            } catch (ex: Exception) {
                _status.value = NetworkErrorMapper.genericConnectionError(
                    getApplication(),
                    ex
                )
            } finally {
                _loading.value = false
            }
        }
    }

    fun actualitzarFiltreCodi(valor: String) {
        _filtreCodi.value = valor
        aplicarFiltres()
    }

    fun actualitzarFiltreMateria(valor: String) {
        _filtreMateria.value = valor
        aplicarFiltres()
    }

    fun actualitzarFiltreDataRecepcio(valor: String) {
        _filtreDataRecepcio.value = valor
        aplicarFiltres()
    }

    fun actualitzarFiltreEstat(valor: String) {
        _filtreEstat.value = valor
        aplicarFiltres()
    }

    fun netejarFiltres() {
        _filtreCodi.value = ""
        _filtreMateria.value = ""
        _filtreDataRecepcio.value = ""
        _filtreEstat.value = ""
        aplicarFiltres()
    }

    private fun aplicarFiltres() {
        val codi = _filtreCodi.value.trim()
        val materia = _filtreMateria.value.trim()
        val dataRecepcio = _filtreDataRecepcio.value.trim()
        val estat = _filtreEstat.value.trim()

        _lots.value = _lotsComplets.value.filter { lot ->
            val coincideixCodi = codi.isBlank() || lot.codiLot.orEmpty().contains(codi, ignoreCase = true)
            val coincideixMateria = materia.isBlank() || lot.materiaPrimaNom.orEmpty().contains(materia, ignoreCase = true)
            val coincideixData = dataRecepcio.isBlank() || lot.dataRecepcio.orEmpty().contains(dataRecepcio, ignoreCase = true)
            val coincideixEstat = estat.isBlank() || lot.estat.orEmpty().equals(estat, ignoreCase = true)

            coincideixCodi && coincideixMateria && coincideixData && coincideixEstat
        }
    }

    fun iniciarLot(id: Long) {
        executarAccioLot(
            id = id,
            missatgeExit = getApplication<Application>().getString(R.string.lots_mobile_started),
            accio = { api, lotId -> api.iniciarLot(lotId) }
        )
    }

    fun finalitzarLot(id: Long) {
        executarAccioLot(
            id = id,
            missatgeExit = getApplication<Application>().getString(R.string.lots_mobile_finished),
            accio = { api, lotId -> api.finalitzarLot(lotId) }
        )
    }

    private fun executarAccioLot(
        id: Long,
        missatgeExit: String,
        accio: suspend (cat.copernic.easytraza_mobile.network.BackendApiService, Long) -> retrofit2.Response<MobileLotDto>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _status.value = ""

                val savedIp = repository.serverIpFlow.first().trim()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))
                val response = accio(api, id)

                if (response.isSuccessful) {
                    _status.value = missatgeExit
                    carregarLots()
                } else {
                    _status.value = getApplication<Application>().getString(R.string.lots_mobile_action_error)
                }

            } catch (ex: Exception) {
                _status.value = NetworkErrorMapper.genericConnectionError(
                    getApplication(),
                    ex
                )
            } finally {
                _loading.value = false
            }
        }
    }
}
