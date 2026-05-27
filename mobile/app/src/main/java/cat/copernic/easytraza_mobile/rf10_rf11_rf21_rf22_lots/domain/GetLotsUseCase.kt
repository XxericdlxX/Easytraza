package cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.domain

import cat.copernic.easytraza_mobile.comu.data.MobileBackendRepository
import cat.copernic.easytraza_mobile.rf07_albarans_ocr.network.dto.MobileLotDto

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
