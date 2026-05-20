package cat.copernic.easytraza_mobile.network

import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.MobileLotDto
import cat.copernic.easytraza_mobile.network.dto.MobileUsuariDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Component de xarxa `ConnectionResponse` de l'aplicació mobile d'EasyTraza.
 */
data class ConnectionResponse(
    val message: String
)

/**
 * Component de xarxa `BackendApiService` de l'aplicació mobile d'EasyTraza.
 */
interface BackendApiService {

    /**
     * Executa l'operació `checkConnection`.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GET("/api/test-connection")
    suspend fun checkConnection(): Response<ConnectionResponse>


    /**
     * Executa l'operació `llistarUsuaris`.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GET("/mobile-api/usuaris")
    suspend fun llistarUsuaris(): List<MobileUsuariDto>

    /**
     * Executa l'operació `llistarLots`.
     * @return resultat obtingut després d'executar l'operació.
     */
    @GET("/mobile-api/lots")
    suspend fun llistarLots(): List<MobileLotDto>

    /**
     * Executa l'operació `iniciarLot`.
     */
    @POST("/mobile-api/lots/{id}/iniciar")
    suspend fun iniciarLot(
        @Path("id") id: Long
    ): Response<MobileLotDto>

    /**
     * Executa l'operació `finalitzarLot`.
     */
    @POST("/mobile-api/lots/{id}/finalitzar")
    suspend fun finalitzarLot(
        @Path("id") id: Long
    ): Response<MobileLotDto>

    /**
     * Executa l'operació `analitzarAlbara`.
     * @param fitxer paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @Multipart
    @POST("/mobile-api/ocr/albarans-proveidor/analitzar")
    suspend fun analitzarAlbara(
        @Part fitxer: MultipartBody.Part
    ): OcrAlbaraResponseDto

    /**
     * Executa l'operació `guardarAlbara`.
     * @param request paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    @POST("/mobile-api/ocr/albarans-proveidor/guardar")
    suspend fun guardarAlbara(
        @Body request: MobileAlbaraSaveRequestDto
    ): Response<Unit>
}
