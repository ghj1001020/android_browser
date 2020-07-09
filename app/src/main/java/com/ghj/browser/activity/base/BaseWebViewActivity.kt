package com.ghj.browser.activity.base

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.dialog.CommonDialog
import com.ghj.browser.util.PermissionUtil
import com.ghj.browser.webkit.OnWebViewListener
import kotlinx.android.synthetic.main.activity_main.*

abstract class BaseWebViewActivity : BaseActivity() , OnWebViewListener {

    private val TAG : String = "BaseWebViewActivity"

    var dialog : CommonDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            showMoveToPermSettingDialog(requestCode)
        }
    }

    // 권한부여위해 설정화면으로 이동 팝업
    fun showMoveToPermSettingDialog( requestCode : Int ) {
        dialog?.dismiss()

        val message = getString( R.string.perm_deny_setting_desc )
        val buttons = arrayOf( getString( R.string.common_cancel) , getString( R.string.common_ok) )
        dialog = CommonDialog( this , 0 , message , buttons , true, TAG ){ dialog, dialogId, selected, data ->
            if( selected == DefineCode.BTN_RIGHT ) {
                moveToPermSetting( requestCode )
            }
        }

        dialog?.show()
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
            // 웹뷰에서 저장권한
            DefineCode.PERM_ID_WEBVIEW_WRITE_EXTERNAL_STORAGE -> {
                if( PermissionUtil.checkPermissions( this , PermissionUtil.WRITE_EXTERNAL_PERMISSION ) == PermissionUtil.PERMISSION_GRANTED ) {
                    wv_main?.onWriteExternalStoragePermissionResult( true )
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
            // 웹뷰에서 저장권한
            DefineCode.PERM_ID_WEBVIEW_WRITE_EXTERNAL_STORAGE -> {
                if( deniedPermissions.size == 0 ) {
                    wv_main?.onWriteExternalStoragePermissionResult( true )
                }
            }
        }
    }
}