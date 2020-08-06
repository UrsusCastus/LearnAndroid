package utils

import android.content.Context
import android.net.*

class Utils {
    //Объект-компаньон имеет доступ ко всем членам класса.
    //Используется для обращения к методам и свойствам объекта через имя содержащего его класса
    // без явного указания имени объекта
    companion object {
        fun isNetworkConnection(context: Context): Boolean {
            //Note: getActiveNetworkInfo() was deprecated in Android 10.
            // Use NetworkCallbacks instead for apps that target Android 10 (API level 29) and higher.
            val connectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.isConnectedOrConnecting
            }
            return false
        }
    }
}
