package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.domain.usecase.config.GetServerIpUseCase
import cat.copernic.easytraza_mobile.domain.usecase.config.SaveServerIpUseCase
import cat.copernic.easytraza_mobile.domain.usecase.config.TestConnectionUseCase
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de configuració de connexió.
 *
 * Gestiona la IP del servidor backend, permet guardar-la de forma persistent i
 * comprova la connexió amb el backend abans d'utilitzar la resta de
 * funcionalitats mobile.
 */
class ConfigIpViewModel(
    application: Application,
    repository: IpPreferencesRepository
) : AndroidViewModel(application) {

    private val getServerIpUseCase = GetServerIpUseCase(repository)
    private val saveServerIpUseCase = SaveServerIpUseCase(repository)
    private val testConnectionUseCase = TestConnectionUseCase()

    private val _ip = MutableStateFlow("")
    val ip: StateFlow<String> = _ip

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    init {
        viewModelScope.launch {
            val savedIp = getServerIpUseCase()
            _ip.value = savedIp
            _status.value = getApplication<Application>()
                .getString(R.string.connection_status_not_tested)
        }
    }

    /**
     * Actualitza el valor de la IP introduïda per l'usuari.
     *
     * @param newIp nou valor escrit al camp de configuració.
     */
    fun onIpChange(newIp: String) {
        _ip.value = newIp
    }

    /**
     * Desa la IP del servidor després de normalitzar-la i validar-la.
     */
    fun saveIp() {
        viewModelScope.launch {
            val normalizedIp = normalizeServerHost(_ip.value)

            if (!isValidServerHost(normalizedIp)) {
                _status.value = getApplication<Application>()
                    .getString(R.string.connection_error_invalid_ip)
                return@launch
            }

            saveServerIpUseCase(normalizedIp)
            _ip.value = normalizedIp
            _status.value = getApplication<Application>()
                .getString(R.string.connection_status_ip_saved)
        }
    }

    /**
     * Comprova si el backend és accessible amb la IP configurada.
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
                val response = testConnectionUseCase(normalizedIp)

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
     * Normalitza el valor introduït perquè només quedi el host del servidor.
     *
     * @param rawValue valor escrit per l'usuari.
     * @return host normalitzat sense protocol, port ni barra final.
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
     * Vàlida si el host indicat és una IPv4 o un alias local acceptat.
     *
     * @param host host normalitzat.
     * @return `true` si el host és vàlid; `false` en cas contrari.
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