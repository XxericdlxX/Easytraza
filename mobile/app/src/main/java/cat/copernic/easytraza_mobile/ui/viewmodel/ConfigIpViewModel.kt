package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Pantalla o component d’interfície `ConfigIpViewModel` de l'aplicació mobile d'EasyTraza.
 */
class ConfigIpViewModel(
    application: Application,
    private val repository: IpPreferencesRepository
) : AndroidViewModel(application) {

    private val _ip = MutableStateFlow("")
    val ip: StateFlow<String> = _ip

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    init {
        viewModelScope.launch {
            val savedIp = repository.serverIpFlow.first()
            _ip.value = savedIp
            _status.value = getApplication<Application>()
                .getString(R.string.connection_status_not_tested)
        }
    }

    /**
     * Executa l'operació `onIpChange`.
     * @param newIp paràmetre necessari per a l'operació.
     */
    fun onIpChange(newIp: String) {
        _ip.value = newIp
    }

    /**
     * Executa l'operació `saveIp`.
     */
    fun saveIp() {
        viewModelScope.launch {
            val normalizedIp = normalizeServerHost(_ip.value)

            if (!isValidServerHost(normalizedIp)) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_invalid_ip)
                return@launch
            }

            repository.saveServerIp(normalizedIp)
            _ip.value = normalizedIp
            _status.value = getApplication<Application>()
                .getString(R.string.connection_status_ip_saved)
        }
    }

    /**
     * Executa l'operació `testConnection`.
     */
    fun testConnection() {
        viewModelScope.launch {
            val normalizedIp = normalizeServerHost(_ip.value)

            if (!isValidServerHost(normalizedIp)) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_invalid_ip)
                return@launch
            }

            try {
                val baseUrl = RetrofitClient.buildBaseUrl(normalizedIp)
                val api = RetrofitClient.create(baseUrl)
                val response = api.checkConnection()

                _status.value = if (response.isSuccessful && response.body() != null) {
                    response.body()?.message
                        ?: getApplication<Application>()
                            .getString(R.string.connection_success_default)
                } else {
                    getApplication<Application>().getString(
                        R.string.connection_error_backend_response,
                        response.code()
                    )
                }
            } catch (ex: Exception) {
                _status.value = NetworkErrorMapper.connectionError(
                    getApplication(),
                    ex
                )
            }
        }
    }

    /**
     * Executa l'operació `normalizeServerHost`.
     * @param rawValue paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun normalizeServerHost(rawValue: String): String {
        return rawValue
            .trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .removeSuffix("/")
            .substringBefore(":")
    }

    /**
     * Executa l'operació `isValidServerHost`.
     * @param host paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    private fun isValidServerHost(host: String): Boolean {
        if (host.isBlank()) {
            return false
        }

        val ipv4Regex = Regex(
            """^((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)$"""
        )

        val localhostAliases = setOf("localhost", "10.0.2.2")

        return host in localhostAliases || ipv4Regex.matches(host)
    }
}
