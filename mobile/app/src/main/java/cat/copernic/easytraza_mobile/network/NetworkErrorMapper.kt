package cat.copernic.easytraza_mobile.network

import android.content.Context
import cat.copernic.easytraza_mobile.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {

    fun connectionError(context: Context, exception: Exception): String {
        return genericConnectionError(context, exception)
    }

    fun genericConnectionError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.connection_error_generic)
        }
    }

    fun ocrError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.ocr_processing_error)
        }
    }

    fun saveAlbaraError(context: Context, exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> context.getString(R.string.connection_error_host)
            is ConnectException -> context.getString(R.string.connection_error_connect)
            is SocketTimeoutException -> context.getString(R.string.connection_error_timeout)
            else -> context.getString(R.string.ocr_save_error)
        }
    }
}
