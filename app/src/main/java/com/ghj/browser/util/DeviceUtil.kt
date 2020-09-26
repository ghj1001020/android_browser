package com.ghj.browser.util

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

object DeviceUtil {

    // GPS On/Off 확인
    fun checkGpsOnOff( context : Context ) : Boolean {
        val lm : LocationManager = context.getSystemService( Context.LOCATION_SERVICE ) as LocationManager
        return lm.isProviderEnabled( LocationManager.GPS_PROVIDER )
    }

    // network check
    fun checkNetworkConnection( context: Context ) : Boolean {
        val cm : ConnectivityManager = context.getSystemService( Context.CONNECTIVITY_SERVICE ) as ConnectivityManager? ?: return false

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
            val capabilities : NetworkCapabilities = cm.getNetworkCapabilities( cm.activeNetwork ) ?: return false

            if( capabilities.hasTransport( NetworkCapabilities.TRANSPORT_WIFI ) || capabilities.hasTransport( NetworkCapabilities.TRANSPORT_CELLULAR ) ) {
                return true
            }
        }

        val networkInfo : NetworkInfo? = cm.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }
}
