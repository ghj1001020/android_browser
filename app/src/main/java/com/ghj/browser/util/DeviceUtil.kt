package com.ghj.browser.util

import android.content.Context
import android.location.LocationManager

object DeviceUtil {

    // GPS On/Off 확인
    fun checkGpsOnOff( context : Context ) : Boolean {
        val lm : LocationManager = context.getSystemService( Context.LOCATION_SERVICE ) as LocationManager
        return lm.isProviderEnabled( LocationManager.GPS_PROVIDER )
    }
}
