package cat.copernic.easytraza_mobile.network

import retrofit2.Response
import retrofit2.http.GET

data class ConnectionResponse(
    val message: String
)

interface BackendApiService {

    @GET("/api/test-connection")
    suspend fun checkConnection(): Response<ConnectionResponse>
}