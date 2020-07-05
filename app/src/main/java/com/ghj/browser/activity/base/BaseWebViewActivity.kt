package com.ghj.browser.activity.base

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.PermissionUtil
import com.ghj.browser.webkit.OnWebViewListener
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseWebViewActivity : BaseActivity() , OnWebViewListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateAfter() {

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    // 웹뷰에서 권한 요청
    override fun onRequestPermissions( requestCode: Int , permissionResult: Int , permissions: Array<String> ) {
        if( permissionResult == PermissionUtil.PERMISSION_DENIED ) {
            PermissionUtil.requestPermissions( this , permissions , requestCode )
        }
        else if( permissionResult == PermissionUtil.PERMISSION_RATIONALE ) {
            moveToPermSetting( requestCode )
        }
    }

    // 액티비티 콜백
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when( requestCode ) {
            // 웹뷰에서 위치권한
            DefineCode.PERM_ID_WEBVIEW_LOCATION -> {
                if( PermissionUtil.checkPermissions( this , PermissionUtil.LOCATION_PERMISSION ) == PermissionUtil.PERMISSION_GRANTED ) {
                    wv_main?.onLocationPermissionResult( true )
                }
                else {
                    wv_main?.onLocationPermissionResult( false )
                }
            }
        }
    }

    override fun onRequestPermissionsResult( requestCode: Int , permissions: Array<out String> , grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val deniedPermissions = mutableListOf<String>()
        for( perm in permissions.withIndex() ) {
            if( perm.index < grantResults.size && grantResults[perm.index] != PackageManager.PERMISSION_GRANTED ) {
                deniedPermissions.add( perm.value )
            }
        }

        when( requestCode ) {
            // 웹뷰에서 위치권한
            DefineCode.PERM_ID_WEBVIEW_LOCATION -> {
                if( deniedPermissions.size == 0) {
                    wv_main?.onLocationPermissionResult( true )
                }
                else {
                    wv_main?.onLocationPermissionResult( false )
                }
            }
        }
    }
}