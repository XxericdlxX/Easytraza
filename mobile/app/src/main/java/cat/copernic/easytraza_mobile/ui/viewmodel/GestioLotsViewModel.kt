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

    private val _lots = MutableStateFlow<List<MobileLotDto>>(emptyList())
    val lots: StateFlow<List<MobileLotDto>> = _lots

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
                _lots.value = api.llistarLots()

                if (_lots.value.isEmpty()) {
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
