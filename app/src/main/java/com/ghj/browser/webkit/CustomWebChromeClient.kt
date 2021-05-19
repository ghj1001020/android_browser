package com.ghj.browser.webkit

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.dialog.AlertDialogFragment
import com.ghj.browser.util.DeviceUtil
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.PermissionUtil

class CustomWebChromeClient : WebChromeClient {

    private val TAG = "CustomWebChromeClient"

    private var context : Context? = null
    private var activity : Activity? = null
    private var listener : OnWebViewListener? = null

    // dialog
    private var dialog : AlertDialogFragment? = null

    // 위치권한
    private var geolocationCallback : GeolocationPermissions.Callback? = null
    private var geolocationOrigin : String? = null


    constructor( context: Context? , listener: OnWebViewListener? ) : super() {
        this.context = context
        this.listener = listener
    }

    // 액티비티 전달
    fun setActivity( activity : Activity ) {
        this.activity = activity
    }

    // 페이지 타이틀
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)

        view?.let {
            listener?.onReceivedTitle( it, title ?: "" )
        }
    }

    // 페이지 로딩 정도
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        view?.let {
            listener?.onProgressChanged( it, newProgress )
        }
    }

    // alert
    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        LogUtil.d("onJsAlert message=" + message )

        if( context == null || (context is Activity && (context as Activity).isFinishing) ) {
            dialog?.dismiss()
            result?.cancel()
            return true
        }

        try {
            dialog?.dismiss()

            val buttons = arrayOf( context?.getString( R.string.common_ok)!! )
            dialog = AlertDialogFragment.newInstance( 0 , "" , message , false , buttons , object : AlertDialogFragment.AlertDialogFragmentInterface{
                override fun onClickListener( dialog: AlertDialogFragment , requestId: Int , selected: Int ) {
                    if( selected == AlertDialogFragment.BTN_SELECT_POSITIVE ) {
                        result?.confirm()
                    }
                    else {
                        result?.cancel()
                    }
                }
            })

            dialog?.show( (context as AppCompatActivity).supportFragmentManager , TAG )
            return true
        }
        catch ( e : Exception ) {
            return false
        }
    }

    // confirm
    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        LogUtil.d("onJsConfirm message=" + message )

        if( context == null || (context is Activity && (context as Activity).isFinishing) ) {
            dialog?.dismiss()
            result?.cancel()
            return true
        }

        try {
            dialog?.dismiss()

            val buttons = arrayOf( context?.getString( R.string.common_ok)!! , context?.getString( R.string.common_cancel)!! )
            dialog = AlertDialogFragment.newInstance( 0 , "" , message , false , buttons , object : AlertDialogFragment.AlertDialogFragmentInterface{
                override fun onClickListener( dialog: AlertDialogFragment , requestId: Int , selected: Int ) {
                    if( selected == AlertDialogFragment.BTN_SELECT_POSITIVE ) {
                        result?.confirm()
                    }
                    else {
                        result?.cancel()
                    }
                }
            })

            dialog?.show( (context as AppCompatActivity).supportFragmentManager , TAG )
            return true
        }
        catch ( e : Exception ) {
            return false
        }
    }


    // 위치권한
    // Geolocation API를 사용할려고 하는데 권한 설정이 되어있지 않음
    override fun onGeolocationPermissionsShowPrompt( origin: String?, callback: GeolocationPermissions.Callback? )
    {
        super.onGeolocationPermissionsShowPrompt(origin, callback)

        LogUtil.d("onGeolocationPermissionsShowPrompt" )

        context?.let {
            val permissions = PermissionUtil.LOCATION_PERMISSION
            val result = PermissionUtil.checkPermissions( it , permissions )

            // 권한거부
            if( result != PermissionUtil.PERMISSION_GRANTED ) {
                geolocationOrigin = origin
                geolocationCallback = callback

                listener?.onRequestPermissions( DefineCode.PERM_ID_WEBVIEW_LOCATION , result , permissions )
            }
            // 권한있음
            else {
                // GPS Off
                if( !DeviceUtil.checkGpsOnOff( it ) ) {
                    callback?.invoke( origin , false , false )
                }
                // GPS On
                else {
                    callback?.invoke( origin , true , true )
                }
            }
        }
    }

    // 위치 권한 요청이 취소됨
    override fun onGeolocationPermissionsHidePrompt()
    {
        super.onGeolocationPermissionsHidePrompt()
        LogUtil.d("onGeolocationPermissionsHidePrompt" )
    }

    // 위치 권한 사용자 선택 후 결과
    fun onLocationPermissionResult( isGranted : Boolean )
    {
        LogUtil.d("onLocationPermissionResult isGranted=" + isGranted )

        if( geolocationCallback == null || TextUtils.isEmpty( geolocationOrigin ) ) {
            return
        }

        if( isGranted ) {
            if( DeviceUtil.checkGpsOnOff( context as Context ) ) {
                geolocationCallback?.invoke( geolocationOrigin , true , false )
            }
            else {
                geolocationCallback?.invoke( geolocationOrigin , false , false )
            }
        }
        else {
            geolocationCallback?.invoke( geolocationOrigin , false , false )
        }
    }

    override fun onShowCustomView(view: View?, requestedOrientation: Int, callback: CustomViewCallback?) {
        super.onShowCustomView(view, requestedOrientation, callback)
        onShowCustomView(view, callback)
    }

    // 동영상 풀스크린 보이기
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        LogUtil.d("onShowCustomView" )

        view?.let {
            listener?.onShowCustomView( it, callback )
        }
    }

    // 동영상 풀스크린 숨기기
    override fun onHideCustomView() {
        super.onHideCustomView()
        LogUtil.d("onHideCustomView" )

        listener?.onHideCustomView()
    }

    override fun getVideoLoadingProgressView(): View? {
        LogUtil.d("getVideoLoadingProgressView" )
        return super.getVideoLoadingProgressView()
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        LogUtil.d("onReceivedIcon" )

        if( view != null ) {
            listener?.onReceivedIcon( view, icon )
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(view: WebView?, callback: ValueCallback<Array<Uri>>?, params: FileChooserParams?) : Boolean {
        if( view == null || callback == null ) {
            return false
        }
        else {
            listener?.onShowFileChooser( view, callback, params)
            return true
        }
    }

    @SuppressWarnings("deprecation")
    fun openFileChooser(callback: ValueCallback<Uri?>, acceptType: String?, capture: String?) {
        listener?.onShowFileChooser(callback)
    }
}