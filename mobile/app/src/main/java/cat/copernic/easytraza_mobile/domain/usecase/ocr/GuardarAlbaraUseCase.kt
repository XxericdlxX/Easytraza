package cat.copernic.easytraza_mobile.domain.usecase.ocr

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import retrofit2.Response

/**
 * Cas d'ús que desa al backend l'albarà de proveïdor revisat des del mobile.
 */
class GuardarAlbaraUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Desa l'albarà revisat amb les seves línies de lot.
     * @param serverHost host o IP del servidor.
     * @param request dades de l'albarà revisades per l'usuari.
     * @return resposta HTTP de l'operació de guardat.
     */
    suspend operator fun invoke(
        serverHost: String,
        request: MobileAlbaraSaveRequestDto
    ): Response<Unit> {
        return repository.guardarAlbara(serverHost, request)
    }
}
