package cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.comu.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.rf07_albarans_ocr.network.dto.MobileLotDto
import cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.domain.FinalitzarLotUseCase
import cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.domain.GetLotsUseCase
import cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.domain.IniciarLotUseCase
import cat.copernic.easytraza_mobile.rf15_configuracio_ip.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.rf15_configuracio_ip.domain.GetServerIpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Locale

/**
 * ViewModel de la pantalla de gestió de lots del client mobile.
 */
class GestioLotsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IpPreferencesRepository(application.applicationContext)
    private val getServerIpUseCase = GetServerIpUseCase(repository)
    private val getLotsUseCase = GetLotsUseCase()
    private val iniciarLotUseCase = IniciarLotUseCase()
    private val finalitzarLotUseCase = FinalitzarLotUseCase()

    private val _lotsComplets = MutableStateFlow<List<MobileLotDto>>(emptyList())

    private val _lots = MutableStateFlow<List<MobileLotDto>>(emptyList())
    val lots: StateFlow<List<MobileLotDto>> = _lots

    private val _codisLotDisponibles = MutableStateFlow<List<String>>(emptyList())
    val codisLotDisponibles: StateFlow<List<String>> = _codisLotDisponibles

    private val _materiesDisponibles = MutableStateFlow<List<String>>(emptyList())
    val materiesDisponibles: StateFlow<List<String>> = _materiesDisponibles

    private val _filtreCodi = MutableStateFlow("")
    val filtreCodi: StateFlow<String> = _filtreCodi

    private val _filtreMateria = MutableStateFlow("")
    val filtreMateria: StateFlow<String> = _filtreMateria

    private val _filtreDataRecepcio = MutableStateFlow("")
    val filtreDataRecepcio: StateFlow<String> = _filtreDataRecepcio

    private val _filtreEstat = MutableStateFlow("")
    val filtreEstat: StateFlow<String> = _filtreEstat

    private val _ordreCamp = MutableStateFlow(ORDRE_DATA_RECEPCIO)
    val ordreCamp: StateFlow<String> = _ordreCamp

    private val _ordreDireccio = MutableStateFlow(ORDRE_DESC)
    val ordreDireccio: StateFlow<String> = _ordreDireccio

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /**
     * Recupera els lots del backend i reaplica els filtres i l'ordenació actuals.
     */
    fun carregarLots() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _status.value = ""

                val savedIp = getServerIpUseCase()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                _lotsComplets.value = getLotsUseCase(savedIp)
                actualitzarOpcionsFiltres()
                aplicarFiltresIOrdenacio()

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

    /**
     * Actualitza el filtre de codi de lot.
     */
    fun actualitzarFiltreCodi(valor: String) {
        _filtreCodi.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza el filtre de matèria primera.
     */
    fun actualitzarFiltreMateria(valor: String) {
        _filtreMateria.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza el filtre de data de recepció.
     */
    fun actualitzarFiltreDataRecepcio(valor: String) {
        _filtreDataRecepcio.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza el filtre d'estat del lot.
     */
    fun actualitzarFiltreEstat(valor: String) {
        _filtreEstat.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza el camp utilitzat per ordenar la llista filtrada.
     */
    fun actualitzarOrdreCamp(valor: String) {
        _ordreCamp.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza la direcció de l'ordenació.
     */
    fun actualitzarOrdreDireccio(valor: String) {
        _ordreDireccio.value = valor
        aplicarFiltresIOrdenacio()
    }

    /**
     * Neteja els filtres sense canviar l'ordenació seleccionada.
     */
    fun netejarFiltres() {
        _filtreCodi.value = ""
        _filtreMateria.value = ""
        _filtreDataRecepcio.value = ""
        _filtreEstat.value = ""
        aplicarFiltresIOrdenacio()
    }

    /**
     * Actualitza els valors disponibles als desplegables de filtre.
     */
    private fun actualitzarOpcionsFiltres() {
        _codisLotDisponibles.value = _lotsComplets.value
            .mapNotNull { it.codiLot?.trim()?.takeIf { valor -> valor.isNotBlank() } }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)

        _materiesDisponibles.value = _lotsComplets.value
            .mapNotNull { it.materiaPrimaNom?.trim()?.takeIf { valor -> valor.isNotBlank() } }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
    }

    /**
     * Aplica els filtres combinats i després ordena el resultat obtingut.
     */
    private fun aplicarFiltresIOrdenacio() {
        val codi = _filtreCodi.value.trim()
        val materia = _filtreMateria.value.trim()
        val dataRecepcio = _filtreDataRecepcio.value.trim()
        val estat = _filtreEstat.value.trim()

        val filtrats = _lotsComplets.value.filter { lot ->
            val coincideixCodi = codi.isBlank() || lot.codiLot.orEmpty().equals(codi, ignoreCase = true)
            val coincideixMateria = materia.isBlank() || lot.materiaPrimaNom.orEmpty().equals(materia, ignoreCase = true)
            val coincideixData = dataRecepcio.isBlank() || lot.dataRecepcio.orEmpty().equals(dataRecepcio, ignoreCase = true)
            val coincideixEstat = estat.isBlank() || lot.estat.orEmpty().equals(estat, ignoreCase = true)

            coincideixCodi && coincideixMateria && coincideixData && coincideixEstat
        }

        _lots.value = ordenarLots(filtrats)
    }

    /**
     * Ordena els lots segons el camp i la direcció seleccionats per l'usuari.
     */
    private fun ordenarLots(lots: List<MobileLotDto>): List<MobileLotDto> {
        val comparator = when (_ordreCamp.value) {
            ORDRE_CODI_LOT -> compareBy { lot: MobileLotDto -> normalitzarText(lot.codiLot) }
            ORDRE_MATERIA -> compareBy { lot: MobileLotDto -> normalitzarText(lot.materiaPrimaNom) }
            ORDRE_PROVEIDOR -> compareBy { lot: MobileLotDto -> normalitzarText(lot.proveidorNom) }
            ORDRE_QUANTITAT -> compareBy { lot: MobileLotDto -> lot.quantitat ?: 0.0 }
            ORDRE_ESTAT -> compareBy { lot: MobileLotDto -> normalitzarText(lot.estat) }
            else -> compareBy { lot: MobileLotDto -> lot.dataRecepcio.orEmpty() }
        }

        val ordenats = lots.sortedWith(comparator.thenBy { it.id })
        return if (_ordreDireccio.value == ORDRE_DESC) ordenats.asReversed() else ordenats
    }

    /**
     * Normalitza un text per fer ordenacions alfabètiques estables.
     */
    private fun normalitzarText(valor: String?): String {
        return valor.orEmpty().lowercase(Locale.ROOT)
    }

    /**
     * Inicia el lot indicat.
     */
    fun iniciarLot(id: Long) {
        executarAccioLot(
            id = id,
            missatgeExit = getApplication<Application>().getString(R.string.lots_mobile_started),
            accio = { lotId -> iniciarLotUseCase(getServerIpUseCase(), lotId) }
        )
    }

    /**
     * Finalitza el lot indicat.
     */
    fun finalitzarLot(id: Long) {
        executarAccioLot(
            id = id,
            missatgeExit = getApplication<Application>().getString(R.string.lots_mobile_finished),
            accio = { lotId -> finalitzarLotUseCase(getServerIpUseCase(), lotId) }
        )
    }

    /**
     * Executa una acció sobre un lot i recarrega la llista si el backend confirma l'operació.
     */
    private fun executarAccioLot(
        id: Long,
        missatgeExit: String,
        accio: suspend (Long) -> Response<MobileLotDto>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _status.value = ""

                val savedIp = getServerIpUseCase()

                if (savedIp.isBlank()) {
                    _status.value = getApplication<Application>().getString(R.string.ocr_error_no_ip)
                    return@launch
                }

                val response = accio(id)

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

    companion object {
        const val ORDRE_CODI_LOT = "codiLot"
        const val ORDRE_MATERIA = "materiaPrima"
        const val ORDRE_PROVEIDOR = "proveidor"
        const val ORDRE_QUANTITAT = "quantitat"
        const val ORDRE_DATA_RECEPCIO = "dataRecepcio"
        const val ORDRE_ESTAT = "estat"
        const val ORDRE_ASC = "ASC"
        const val ORDRE_DESC = "DESC"
    }
}