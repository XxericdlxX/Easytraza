package cat.copernic.easytraza_mobile.rf10_rf11_rf21_rf22_lots.domain

import cat.copernic.easytraza_mobile.comu.data.MobileBackendRepository
import cat.copernic.easytraza_mobile.rf07_albarans_ocr.network.dto.MobileLotDto
import retrofit2.Response

/**
 * Cas d'ús que finalitza un lot de proveïdor des del client mobile.
 */
class FinalitzarLotUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Demana al backend que tanqui el lot indicat.
     * @param serverHost host o IP del servidor.
     * @param id identificador del lot.
     * @return resposta HTTP amb el lot actualitzat.
     */
    suspend operator fun invoke(serverHost: String, id: Long): Response<MobileLotDto> {
        return repository.finalitzarLot(serverHost, id)
    }
}
