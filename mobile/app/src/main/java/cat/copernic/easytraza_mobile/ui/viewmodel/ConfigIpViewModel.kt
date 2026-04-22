package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

    fun onIpChange(newIp: String) {
        _ip.value = newIp
    }

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
            } catch (_: UnknownHostException) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_host)
            } catch (_: ConnectException) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_connect)
            } catch (_: SocketTimeoutException) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_timeout)
            } catch (_: IllegalArgumentException) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_invalid_ip)
            } catch (_: Exception) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_generic)
            }
        }
    }

    private fun normalizeServerHost(rawValue: String): String {
        return rawValue
            .trim()
            .removePrefix("http://")
            .removeSuffix("/")
            .substringBefore(":")
    }

    private fun isValidServerHost(host: String): Boolean {
        if (host.isBlank()) return false

        val ipv4Regex = Regex(
            """^((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)$"""
        )

        val localhostAliases = setOf("localhost", "10.0.2.2")

        return host in localhostAliases || ipv4Regex.matches(host)
    }
}