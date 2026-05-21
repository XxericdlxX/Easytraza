package cat.copernic.easytraza_mobile.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Component de xarxa `RetrofitClient` de l'aplicació mobile d'EasyTraza.
 */
object RetrofitClient {

    private const val DEFAULT_SCHEME = "http"
    private const val DEFAULT_PORT = 8080

    /**
     * Executa l'operació `buildBaseUrl`.
     * @param serverHost paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun buildBaseUrl(serverHost: String): String {
        val cleanHost = serverHost
            .trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .removeSuffix("/")
            .substringBefore(":")

        require(cleanHost.isNotBlank()) { "El host del servidor no pot estar buit." }

        return "$DEFAULT_SCHEME://$cleanHost:$DEFAULT_PORT/"
    }

    /**
     * Executa l'operació `create`.
     * @param baseUrl paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun create(baseUrl: String): BackendApiService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }
}
