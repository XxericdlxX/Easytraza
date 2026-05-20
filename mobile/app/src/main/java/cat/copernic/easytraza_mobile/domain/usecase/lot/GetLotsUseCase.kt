package cat.copernic.easytraza_mobile.domain.usecase.lot

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto

/**
 * Cas d'ús que obté els lots de proveïdor gestionables des del mobile.
 */
class GetLotsUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Recupera els lots registrats al backend.
     * @param serverHost host o IP del servidor.
     * @return llista de lots de proveïdor.
     */
    suspend operator fun invoke(serverHost: String): List<MobileLotDto> {
        return repository.llistarLots(serverHost)
    }
}
