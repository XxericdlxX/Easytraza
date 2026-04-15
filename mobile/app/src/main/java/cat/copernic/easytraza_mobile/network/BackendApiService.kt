package cat.copernic.easytraza_mobile.network

import retrofit2.Response
import retrofit2.http.GET

interface BackendApiService {

    @GET("/")
    suspend fun checkConnection(): Response<String>
}