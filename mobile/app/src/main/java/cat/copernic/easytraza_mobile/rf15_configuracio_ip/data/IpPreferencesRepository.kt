package cat.copernic.easytraza_mobile.rf15_configuracio_ip.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "easytraza_settings")

/**
 * Component de dades `IpPreferencesRepository` de l'aplicació mobile d'EasyTraza.
 */
class IpPreferencesRepository(private val context: Context) {

    companion object {
        private val SERVER_IP_KEY = stringPreferencesKey("server_ip")
    }

    val serverIpFlow: Flow<String> = context.dataStore.data.map { preferences: Preferences ->
        preferences[SERVER_IP_KEY] ?: "10.0.2.2"
    }

    /**
     * Executa l'operació `saveServerIp`.
     * @param ip paràmetre necessari per a l'operació.
     */
    suspend fun saveServerIp(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_IP_KEY] = ip
        }
    }
}
