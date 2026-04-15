package cat.copernic.easytraza_mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class IpPreferencesRepository(private val context: Context) {

    companion object {
        private val SERVER_IP_KEY = stringPreferencesKey("server_ip")
    }

    val serverIpFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SERVER_IP_KEY] ?: "10.0.2.2"
    }

    suspend fun saveServerIp(ip: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_IP_KEY] = ip
        }
    }
}