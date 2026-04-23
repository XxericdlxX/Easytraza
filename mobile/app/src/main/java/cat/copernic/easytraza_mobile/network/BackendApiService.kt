package cat.copernic.easytraza_mobile.network

import cat.copernic.easytraza_mobile.network.dto.MobileAlbaraSaveRequestDto
import cat.copernic.easytraza_mobile.network.dto.OcrAlbaraResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ConnectionResponse(
    val message: String
)

interface BackendApiService {

    @GET("/api/test-connection")
    suspend fun checkConnection(): Response<ConnectionResponse>

    @Multipart
    @POST("/mobile-api/ocr/albarans-proveidor/analitzar")
    suspend fun analitzarAlbara(
        @Part fitxer: MultipartBody.Part
    ): OcrAlbaraResponseDto

    @POST("/mobile-api/ocr/albarans-proveidor/guardar")
    suspend fun guardarAlbara(
        @Body request: MobileAlbaraSaveRequestDto
    ): Response<Unit>
}