package cat.copernic.easytraza_mobile.domain.usecase.config

import cat.copernic.easytraza_mobile.data.IpPreferencesRepository
import kotlinx.coroutines.flow.first

/**
 * Cas d'ús que obté la IP o host del servidor configurat al dispositiu.
 */
class GetServerIpUseCase(
    private val repository: IpPreferencesRepository
) {

    /**
     * Recupera el valor guardat i elimina espais innecessaris.
     * @return IP o host configurat per connectar amb el backend.
     */
    suspend operator fun invoke(): String {
        return repository.serverIpFlow.first().trim()
    }
}
