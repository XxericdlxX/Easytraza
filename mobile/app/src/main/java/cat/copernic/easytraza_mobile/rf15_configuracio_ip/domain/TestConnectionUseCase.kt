package cat.copernic.easytraza_mobile.rf15_configuracio_ip.domain

import cat.copernic.easytraza_mobile.comu.data.MobileBackendRepository
import cat.copernic.easytraza_mobile.comu.network.ConnectionResponse
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
