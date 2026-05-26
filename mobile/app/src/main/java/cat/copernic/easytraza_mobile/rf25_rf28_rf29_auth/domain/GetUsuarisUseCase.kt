package cat.copernic.easytraza_mobile.rf25_rf28_rf29_auth.domain

import cat.copernic.easytraza_mobile.comu.data.MobileBackendRepository
import cat.copernic.easytraza_mobile.rf25_rf28_rf29_auth.network.dto.MobileUsuariDto

/**
 * Cas d'ús que obté els usuaris disponibles per identificar-se a l'app mobile.
 */
class GetUsuarisUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Recupera els usuaris identificables des del backend.
     * @param serverHost host o IP del servidor.
     * @return llista d'usuaris disponibles.
     */
    suspend operator fun invoke(serverHost: String): List<MobileUsuariDto> {
        return repository.llistarUsuaris(serverHost)
    }
}