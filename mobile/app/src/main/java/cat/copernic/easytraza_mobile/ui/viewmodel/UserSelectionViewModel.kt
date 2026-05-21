package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.domain.usecase.config.GetServerIpUseCase
import cat.copernic.easytraza_mobile.domain.usecase.usuari.GetUsuarisUseCase
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de la pantalla de selecció d'usuari.
 *
 * Gestiona la càrrega dels usuaris identificables des del backend mobile i manté
 * l'estat necessari perquè la interfície mostri la llista d'usuaris, l'estat de
 * càrrega i els missatges d'error.
 */
class UserSelectionViewModel(
    application: Application,
    repository: IpPreferencesRepository
) : AndroidViewModel(application) {

    private val getServerIpUseCase = GetServerIpUseCase(repository)
    private val getUsuarisUseCase = GetUsuarisUseCase()

    private val _users = MutableStateFlow<List<MobileUsuariDto>>(emptyList())
    val users: StateFlow<List<MobileUsuariDto>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    init {
        carregarUsuaris()
    }

    /**
     * Carrega els usuaris disponibles per a la identificació mobile.
     *
     * Si no hi ha una IP configurada, deixa la llista buida i mostra un missatge
     * informatiu. Si la petició falla, converteix l'error tècnic en un missatge
     * comprensible per a l'usuari.
     */
    fun carregarUsuaris() {
        viewModelScope.launch {
            _loading.value = true
            _status.value = ""

            try {
                val savedIp = getServerIpUseCase()

                if (savedIp.isBlank()) {
                    _users.value = emptyList()
                    _status.value = getApplication<Application>()
                        .getString(R.string.mobile_user_error_no_ip)
                    return@launch
                }

                val usersResposta = getUsuarisUseCase(savedIp)

                _users.value = usersResposta
                _status.value = if (usersResposta.isEmpty()) {
                    getApplication<Application>().getString(R.string.mobile_user_empty)
                } else {
                    ""
                }

            } catch (ex: Exception) {
                _users.value = emptyList()
                _status.value = NetworkErrorMapper.connectionError(
                    getApplication(),
                    ex
                )
            } finally {
                _loading.value = false
            }
        }
    }
}