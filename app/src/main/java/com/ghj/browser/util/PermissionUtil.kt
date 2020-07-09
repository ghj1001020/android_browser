package com.ghj.browser.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionUtil {

    const val PERMISSION_RATIONALE = -2
    const val PERMISSION_DENIED = -1
    const val PERMISSION_GRANTED = 0

    // 위치 권한
    val LOCATION_PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION)
    // 저장 권한
    val WRITE_EXTERNAL_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)


    // 권한 있는지 확인
    fun checkPermissions( activity : Activity , permissions : Array<String> ) : Int
    {
        if( Build.VERSION_CODES.M > Build.VERSION.SDK_INT ) {
            return PERMISSION_GRANTED
        }

        if( permissions.size == 0 ) {
            return PERMISSION_GRANTED
        }

        var deniedPermissions : MutableList<String> = mutableListOf()   // 없는 권한들
        var isRationale = false
        for( perm in permissions ) {
            if( ContextCompat.checkSelfPermission( activity , perm ) != PackageManager.PERMISSION_GRANTED ) {
                deniedPermissions.add( perm )

                // 이전에 거부한적 있는지
                if( ActivityCompat.shouldShowRequestPermissionRationale( activity , perm ) ) {
                    isRationale = true
                }
            }
        }

        if( deniedPermissions.size > 0 && !isRationale ) {
            return PERMISSION_DENIED
        }
        else if( deniedPermissions.size > 0 && isRationale ) {
            return PERMISSION_RATIONALE
        }
        else {
            return PERMISSION_GRANTED
        }
    }

    fun checkPermissions( context : Context , permissions : Array<String> ) : Int {
        if( Build.VERSION_CODES.M > Build.VERSION.SDK_INT ) {
            return PERMISSION_GRANTED
        }

        if( permissions.size == 0 ) {
            return PERMISSION_GRANTED
        }

        var deniedPermissions : MutableList<String> = mutableListOf()   // 없는 권한들
        for( perm in permissions ) {
            if( ContextCompat.checkSelfPermission( context , perm ) != PackageManager.PERMISSION_GRANTED ) {
                deniedPermissions.add( perm )
            }
        }

        if( deniedPermissions.size > 0 ) {
            return PERMISSION_DENIED
        }
        else {
            return PERMISSION_GRANTED
        }
    }

    // 권한 요청
    fun requestPermissions( activity : Activity , permissions : Array<String> , requestCode : Int )
    {
        ActivityCompat.requestPermissions( activity , permissions , requestCode )
    }
}