package cat.copernic.easytraza_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.easytraza_mobile.R
import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import cat.copernic.easytraza_mobile.network.NetworkErrorMapper
import cat.copernic.easytraza_mobile.network.RetrofitClient
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserSelectionViewModel(
    application: Application,
    private val repository: IpPreferencesRepository
) : AndroidViewModel(application) {

    private val _users = MutableStateFlow<List<MobileUsuariDto>>(emptyList())
    val users: StateFlow<List<MobileUsuariDto>> = _users

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status

    init {
        carregarUsuaris()
    }

    fun carregarUsuaris() {
        viewModelScope.launch {
            _loading.value = true
            _status.value = ""

            try {
                val savedIp = repository.serverIpFlow.first().trim()

                if (savedIp.isBlank()) {
                    _users.value = emptyList()
                    _status.value = getApplication<Application>()
                        .getString(R.string.mobile_user_error_no_ip)
                    return@launch
                }

                val api = RetrofitClient.create(RetrofitClient.buildBaseUrl(savedIp))
                val usersResposta = api.llistarUsuaris()

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
