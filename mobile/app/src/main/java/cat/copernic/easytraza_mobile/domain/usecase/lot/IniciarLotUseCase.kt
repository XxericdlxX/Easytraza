package cat.copernic.easytraza_mobile.domain.usecase.lot

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import retrofit2.Response

/**
 * Cas d'ús que inicia un lot de proveïdor des del client mobile.
 */
class IniciarLotUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Demana al backend que passi el lot a estat obert.
     * @param serverHost host o IP del servidor.
     * @param id identificador del lot.
     * @return resposta HTTP amb el lot actualitzat.
     */
    suspend operator fun invoke(serverHost: String, id: Long): Response<MobileLotDto> {
        return repository.iniciarLot(serverHost, id)
    }
}
