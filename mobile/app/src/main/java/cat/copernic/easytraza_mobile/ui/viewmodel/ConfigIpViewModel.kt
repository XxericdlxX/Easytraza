package cat.copernic.easytraza_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConfigIpViewModel(
    private val repository: IpPreferencesRepository
) : ViewModel() {

    private val _ip = MutableStateFlow("")
    val ip: StateFlow<String> = _ip

    private val _status = MutableStateFlow("Sin probar conexión")
    val status: StateFlow<String> = _status

    init {
        viewModelScope.launch {
            _ip.value = repository.serverIpFlow.first()
        }
    }

    fun onIpChange(newIp: String) {
        _ip.value = newIp
    }

    fun saveIp() {
        viewModelScope.launch {
            repository.saveServerIp(_ip.value)
            _status.value = "IP guardada correctamente"
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            try {
                val baseUrl = "http://${_ip.value}:8080/"
                val api = RetrofitClient.create(baseUrl)
                val response = api.checkConnection()

                _status.value = if (response.isSuccessful) {
                    "Conexión correcta con el backend"
                } else {
                    "El backend responde pero con error: ${response.code()}"
                }
            } catch (e: Exception) {
                _status.value = "Error de conexión: ${e.message}"
            }
        }
    }
}