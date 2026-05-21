package cat.copernic.easytraza_mobile.domain.usecase.usuari

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto

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
