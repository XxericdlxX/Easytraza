package cat.copernic.easytraza_mobile.network

import android.content.Context
import cat.copernic.easytraza_mobile.R
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {

    fun connectionError(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> context.getString(R.string.network_error_unknown_host)
            is ConnectException -> context.getString(R.string.network_error_no_connection)
            is SocketTimeoutException -> context.getString(R.string.network_error_timeout)
            is IllegalArgumentException -> context.getString(R.string.network_error_invalid_ip)
            else -> context.getString(R.string.network_error_generic)
        }
    }

    fun ocrError(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> context.getString(R.string.network_error_unknown_host)
            is ConnectException -> context.getString(R.string.ocr_error_connection)
            is SocketTimeoutException -> context.getString(R.string.ocr_error_timeout)
            is IllegalArgumentException -> context.getString(R.string.network_error_invalid_ip)
            else -> context.getString(R.string.ocr_processing_error)
        }
    }

    fun saveAlbaraError(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> context.getString(R.string.network_error_unknown_host)
            is ConnectException -> context.getString(R.string.network_error_no_connection)
            is SocketTimeoutException -> context.getString(R.string.network_error_timeout)
            is IllegalArgumentException -> context.getString(R.string.network_error_invalid_ip)
            else -> context.getString(R.string.ocr_save_error)
        }
    }
}