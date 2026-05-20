package cat.copernic.easytraza_mobile.data.repository

import cat.copernic.easytraza_mobile.network.BackendApiService
import cat.copernic.easytraza_mobile.network.ConnectionResponse
import cat.copernic.easytraza_mobile.network.RetrofitClient
import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import okhttp3.MultipartBody
import retrofit2.Response

/**
 * Repositori de dades que centralitza l'accés del client mobile al backend.
 *
 * Aquesta classe forma part de la capa Data i evita que els ViewModels hagin
 * de construir directament Retrofit o conèixer els detalls de l'API remota.
 */
class MobileBackendRepository {

    /**
     * Comprova la connexió amb el backend configurat.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @return resposta HTTP del backend.
     */
    suspend fun provarConnexio(serverHost: String): Response<ConnectionResponse> {
        return crearApi(serverHost).checkConnection()
    }

    /**
     * Recupera els usuaris identificables del backend.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @return llista d'usuaris disponibles per seleccionar a l'app.
     */
    suspend fun llistarUsuaris(serverHost: String): List<MobileUsuariDto> {
        return crearApi(serverHost).llistarUsuaris()
    }

    /**
     * Recupera els lots de proveïdor disponibles per gestionar al mobile.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @return llista de lots retornada pel backend.
     */
    suspend fun llistarLots(serverHost: String): List<MobileLotDto> {
        return crearApi(serverHost).llistarLots()
    }

    /**
     * Sol·licita al backend l'inici d'un lot.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @param id identificador del lot que s'ha d'iniciar.
     * @return resposta HTTP amb el lot actualitzat si l'operació és correcta.
     */
    suspend fun iniciarLot(serverHost: String, id: Long): Response<MobileLotDto> {
        return crearApi(serverHost).iniciarLot(id)
    }

    /**
     * Sol·licita al backend la finalització d'un lot.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @param id identificador del lot que s'ha de finalitzar.
     * @return resposta HTTP amb el lot actualitzat si l'operació és correcta.
     */
    suspend fun finalitzarLot(serverHost: String, id: Long): Response<MobileLotDto> {
        return crearApi(serverHost).finalitzarLot(id)
    }

    /**
     * Envia un document d'albarà al backend perquè sigui processat per OCR.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @param fitxer part multipart amb el document seleccionat o capturat.
     * @return dades detectades pel backend mitjançant OCR.
     */
    suspend fun analitzarAlbara(
        serverHost: String,
        fitxer: MultipartBody.Part
    ): OcrAlbaraResponseDto {
        return crearApi(serverHost).analitzarAlbara(fitxer)
    }

    /**
     * Desa un albarà de proveïdor revisat des del mobile.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @param request dades revisades de l'albarà i dels lots.
     * @return resposta HTTP de l'operació de guardat.
     */
    suspend fun guardarAlbara(
        serverHost: String,
        request: MobileAlbaraSaveRequestDto
    ): Response<Unit> {
        return crearApi(serverHost).guardarAlbara(request)
    }

    /**
     * Construeix el servei Retrofit per al servidor indicat.
     * @param serverHost host o IP del servidor configurat al mobile.
     * @return servei Retrofit preparat per consumir l'API.
     */
    private fun crearApi(serverHost: String): BackendApiService {
        return RetrofitClient.create(RetrofitClient.buildBaseUrl(serverHost))
    }
}
