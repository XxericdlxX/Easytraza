package cat.copernic.easytraza_mobile.rf15_configuracio_ip.domain

import cat.copernic.easytraza_mobile.rf15_configuracio_ip.data.IpPreferencesRepository

/**
 * Cas d'ús que desa la IP o host del servidor al DataStore del dispositiu.
 */
class SaveServerIpUseCase(
    private val repository: IpPreferencesRepository
) {

    /**
     * Guarda el valor normalitzat de la IP o host del backend.
     * @param serverHost host del backend que es vol conservar.
     */
    suspend operator fun invoke(serverHost: String) {
        repository.saveServerIp(serverHost)
    }
}
