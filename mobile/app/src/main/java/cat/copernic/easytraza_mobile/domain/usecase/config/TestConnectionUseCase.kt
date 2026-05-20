package cat.copernic.easytraza_mobile.domain.usecase.config

import cat.copernic.easytraza_mobile.data.repository.MobileBackendRepository
import cat.copernic.easytraza_mobile.network.ConnectionResponse
import retrofit2.Response

/**
 * Cas d'ús que comprova si el client mobile pot contactar amb el backend.
 */
class TestConnectionUseCase(
    private val repository: MobileBackendRepository = MobileBackendRepository()
) {

    /**
     * Executa la comprovació de connexió contra el backend configurat.
     * @param serverHost host o IP del servidor.
     * @return resposta HTTP del servei de test de connexió.
     */
    suspend operator fun invoke(serverHost: String): Response<ConnectionResponse> {
        return repository.provarConnexio(serverHost)
    }
}
