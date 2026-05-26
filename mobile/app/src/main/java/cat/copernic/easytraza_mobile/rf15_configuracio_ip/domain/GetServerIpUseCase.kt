package cat.copernic.easytraza_mobile.rf15_configuracio_ip.domain

import cat.copernic.easytraza_mobile.rf15_configuracio_ip.data.IpPreferencesRepository
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
