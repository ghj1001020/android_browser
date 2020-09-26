package com.ghj.browser.activity.base

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.webkit.ValueCallback
import android.webkit.WebBackForwardList
import android.webkit.WebView
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.dialog.CommonDialog
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.PermissionUtil
import com.ghj.browser.webkit.OnWebViewListener
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URLDecoder

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

    // 뒤로/앞으로 갈 경우의 인덱스를 리턴
    fun getWillMovePageStep( _webView: WebView? , isBack : Boolean ) : Int? {
        val historyList : WebBackForwardList? = _webView?.copyBackForwardList()
        if( historyList == null || historyList.size <= 1 ) {
            return null
        }
        else {
            var move : Int? = null
            if( isBack ) {
                for( i in historyList.currentIndex..0 ) {
                    val history = historyList.getItemAtIndex( i ).url

                    if( !DefineCode.ERROR_PAGE.equals( history ) ) {
                        move = i
                        break
                    }
                }
            }
            else {
                for( i in historyList.currentIndex until historyList.size) {
                    val history = historyList.getItemAtIndex( i ).url

                    if( !DefineCode.ERROR_PAGE.equals( history ) ) {
                        move = i
                        break
                    }
                }
            }

            return move
        }
    }


    // Native -> Js 호출
    fun callJsFunction(webView : WebView?, funcName : String, params : Array<String> ) {
        try {
            var script = "javascript:" + funcName +"("

            var param = ""
            if( params.isNotEmpty() )
            {
                param = TextUtils.join( "', '" , params )
                script += "'" + param + "'"
            }

            script += ");"

            LogUtil.d( TAG , "callJavaScript : ${script}" )

            Thread( Runnable {
                webView?.let {
                    runOnUiThread {
                        it.loadUrl( script )
                    }
                }
            }).start()
        }
        catch ( e : Exception )
        {
            LogUtil.e( TAG , "callJavaScript err : " + e.message )
        }
    }

    // Native -> Js 호출 후 값리턴
    @TargetApi( Build.VERSION_CODES.KITKAT )
    fun callJsFunction(webView : WebView?, funcName : String, params : Array<String> , handler : Handler , what : Int ) {
        try {
            var script = "javascript:" + funcName +"("

            var param = ""
            if( params.isNotEmpty() )
            {
                param = TextUtils.join( "', '" , params )
                script += "'" + param + "'"
            }

            script += ");"

            val javascript = script
            LogUtil.d( TAG , "callJavaScript : " + javascript )
            val rtnHandler : Handler = handler
            val rtnWhat : Int = what

            Thread( Runnable {
                webView?.let {
                    runOnUiThread {
                        it.evaluateJavascript( javascript ) { value : String? ->
                            val message : Message = rtnHandler.obtainMessage( rtnWhat )
                            val bundle : Bundle = Bundle()
                            bundle.putString( DefineCode.HDL_PARAM_WV_BRIDGE_RTN , value )
                            message.data = bundle

                            rtnHandler.sendMessage( message )
                        }
                    }
                }
            }).start()
        }
        catch ( e : Exception )
        {
            LogUtil.e( TAG , "callJavaScriptReturn err : " + e.message )
        }
    }

    // URL 리다이렉트 - URL 타입별 처리
    override fun shouldOverrideLoading( _webView: WebView , urlType: Int , url: String , isRedirect: Boolean ) {
        // intent: 스키마
        if( urlType == DefineCode.URL_TYPE_INTENT ) {
            moveToIntentUrl( url )
        }
        // sms: smsto:
        else if( urlType == DefineCode.URL_TYPE_SMS ) {
            moveToSms( url )
        }
    }

    // Intent 이동
    fun moveToIntentUrl( url : String ) {
        val intent : Intent = Intent.parseUri( url , Intent.URI_INTENT_SCHEME )
        val uri : Uri? = intent.data

        try {
            uri?.let {
                // launch url page
                try {
                    val sendIntent : Intent = Intent( Intent.ACTION_VIEW , it )
                    sendIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
                    startActivity( sendIntent )
                }
                catch ( e : Exception ) {
                    // 설치되어 있는 경우
                    val appIntent : Intent? = packageManager.getLaunchIntentForPackage( intent.`package` ?: "" )
                    if( appIntent != null ) {
                        appIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
                        startActivity( appIntent )
                    }
                    // 설치안되어 있는 경우 ( 구글플레이 열기 )
                    else {
                        val marketIntent : Intent = Intent( Intent.ACTION_VIEW , Uri.parse( DefineCode.SCHEME_APP_MARKET + intent.`package` ) )
                        marketIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK )
                        startActivity( marketIntent )
                    }
                }
            }
        }
        catch ( e1 : Exception ) {
            LogUtil.e( TAG , "err=${e1.localizedMessage}")
        }
    }

    // sms 메시지앱 이동
    fun moveToSms( url : String ) {
        try {
            val intent : Intent = Intent( Intent.ACTION_SENDTO, Uri.parse( url ) )

            if( url.contains( "body=" ) ) {
                var body = url.split( "body=" )[1]
                try {
                    body = URLDecoder.decode( body , "UTF-8" )
                    intent.putExtra( "sms_body" , body )
                }
                catch ( e : Exception ) {
                    LogUtil.e( TAG , "err=" + e.localizedMessage )
                }
            }

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity( intent )
        }
        catch ( e : Exception ) {
            LogUtil.e( TAG , "err=${e.localizedMessage}" )
        }
    }
}