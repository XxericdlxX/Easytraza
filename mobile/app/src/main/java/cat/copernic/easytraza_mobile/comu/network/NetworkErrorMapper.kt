package cat.copernic.easytraza_mobile.comu.network

import android.content.Context
import cat.copernic.easytraza_mobile.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Component de xarxa `NetworkErrorMapper` de l'aplicació mobile d'EasyTraza.
 */
object NetworkErrorMapper {

    /**
     * Executa l'operació `connectionError`.
     * @param context paràmetre necessari per a l'operació.
     * @param exception paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun connectionError(context: Context, exception: Exception): String {
        return genericConnectionError(context, exception)
    }

    /**
     * Executa l'operació `genericConnectionError`.
     * @param context paràmetre necessari per a l'operació.
     * @param exception paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun genericConnectionError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.connection_error_generic)
        }
    }

    /**
     * Executa l'operació `ocrError`.
     * @param context paràmetre necessari per a l'operació.
     * @param exception paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun ocrError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.ocr_processing_error)
        }
    }

    /**
     * Executa l'operació `saveAlbaraError`.
     * @param context paràmetre necessari per a l'operació.
     * @param exception paràmetre necessari per a l'operació.
     * @return resultat obtingut després d'executar l'operació.
     */
    fun saveAlbaraError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.ocr_save_error)
        }
    }
}