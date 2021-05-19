package com.ghj.browser.webkit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.DeviceUtil
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.StringUtil
import com.ghj.browser.util.getFileExtension
import java.util.*

class CustomWebViewClient : WebViewClient {

    private val TAG : String = "CustomWebViewClient"

    private var context : Context? = null
    private var listener : OnWebViewListener? = null

    private var startUrl : String = ""  // 페이지 로딩 url
    private val FILTER_REOUSRCES_URL = arrayOf("png", "jpg", "jpeg", "gif", "json", "js", "css", "ico", "icon", "woff2")    // URL에


    constructor( context: Context? , listener: OnWebViewListener? ) : super() {
        this.context = context
        this.listener = listener
    }


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if( view == null || TextUtils.isEmpty( url ) ) {
            return
        }

        // url 타입 체크
        val type = checkCatchUrlType( url )

        LogUtil.d("onPageStarted type=" + type + " , url=" + url )

        if( type != DefineCode.URL_TYPE_HTTP ) {
            listener?.onPageStarted( view, type, url!! )
            return
        }

        if( this.context == null ) {
            return
        }

        // 네트워크 체크
        if( !DeviceUtil.checkNetworkConnection( this.context as Context ) ) {
            if( url != null && !url.startsWith( DefineCode.ERROR_PAGE ) ) {
                val msg = (context as Context).resources.getString( R.string.webview_error_network )
                listener?.onReceivedError( view , msg, url )
            }
            return
        }

        listener?.onPageStarted( view, type, url!! )
        startUrl = url ?: ""

        super.onPageStarted(view, url, favicon)
    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if( view == null || TextUtils.isEmpty( url ) ) {
            return false
        }

        // url 타입 체크
        val type = checkCatchUrlType( url )

        LogUtil.d("shouldOverrideUrlLoading1 type=" + type + " , url=" + url )

        if( type != DefineCode.URL_TYPE_HTTP ) {
            listener?.shouldOverrideLoading( view, type, url as String, false )
            return true
        }

        if( this.context == null ) {
            return false
        }

        // 네트워크 체크
        if( !DeviceUtil.checkNetworkConnection( this.context as Context ) ) {
            if( url != null && !url.startsWith( DefineCode.ERROR_PAGE ) ) {
                val msg = (context as Context).resources.getString( R.string.webview_error_network )
                listener?.onReceivedError( view , msg, url )
            }
            return true
        }

        startUrl = url ?: ""

        return false
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if( view == null || request == null ) {
            return false
        }

        val url : String = request.url.toString()

        // url 타입 체크
        val type = checkCatchUrlType( url )

        LogUtil.d("shouldOverrideUrlLoading2 type=" + type + " , url=" + url + " , isRedirect=" + request.isRedirect )

        if( type != DefineCode.URL_TYPE_HTTP ) {
            listener?.shouldOverrideLoading( view, type, url, request.isRedirect )  // isRedirect : 요청이 서버측 리다이렉션의 결과인지 여부
            return true
        }

        if( this.context == null ) {
            return false
        }

        // 네트워크 체크
        if( !DeviceUtil.checkNetworkConnection( this.context as Context ) ) {
            if( !url.startsWith( DefineCode.ERROR_PAGE ) ) {
                val msg = (context as Context).resources.getString( R.string.webview_error_network )
                listener?.onReceivedError( view , msg, url )
            }
            return true
        }

        startUrl = url ?: ""

        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if( view == null || TextUtils.isEmpty( url ) ) {
            return
        }

        listener?.onPageFinished( view, url!! )

        LogUtil.d("onPageFinished url=" + url )
        super.onPageFinished(view, url)
    }

    // 애플리케이션에 자원 요청을 알리고 애플리케이션이 데이터를 리턴
    @SuppressWarnings("deprecation")
    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
//        LogUtil.d( TAG , "shouldInterceptRequest1 url=" + url )

        return super.shouldInterceptRequest(view, url)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
//        LogUtil.d( TAG , "shouldInterceptRequest2 url=" + request?.url )

        return super.shouldInterceptRequest(view, request)
    }

    // WebView가 지정된 URL로 지정된 자원을로드 할 것임을 알림
    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)

        if( view == null || TextUtils.isEmpty(url) ) {
            return
        }

        if( !url!!.startsWith("https://") && !url.startsWith("http://") ) {
            return
        }

        val pureUrl = StringUtil.removeParameterFromUrl(url)
        val pureUrlExt = StringUtil.getUrlExtension(pureUrl)
        if( !FILTER_REOUSRCES_URL.contains(pureUrlExt.toLowerCase(Locale.getDefault())) ) {
//            LogUtil.d( TAG , "onLoadResource ext=$pureUrlExt, url=$url")
            listener?.onLoadResource(view, url!!)
        }
    }

    @SuppressWarnings("deprecation")
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.M ) {
            return
        }

        if( view == null ) {
            return
        }

        if( startUrl.equals( failingUrl ) ) {
            when ( errorCode ) {
                ERROR_HOST_LOOKUP, ERROR_CONNECT, ERROR_TIMEOUT, ERROR_UNSUPPORTED_SCHEME -> {
                    val msg = (context as Context).resources.getString( R.string.webview_error_code )
                    listener?.onReceivedError( view , msg.format( errorCode), failingUrl )
                }
            }
        }

        LogUtil.d("onReceivedError1 errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)

        if( view == null ) {
            return
        }

        val errorCode = error?.errorCode ?: WebViewClient.ERROR_UNKNOWN
        error?.description
        val failingUrl = request?.url.toString()

        if( startUrl.equals( failingUrl ) ) {
            when ( errorCode ) {
                ERROR_HOST_LOOKUP, ERROR_CONNECT, ERROR_TIMEOUT, ERROR_UNSUPPORTED_SCHEME, ERROR_FAILED_SSL_HANDSHAKE -> {
                    val msg = (context as Context).resources.getString( R.string.webview_error_code )
                    listener?.onReceivedError( view , msg.format( errorCode), failingUrl )
                }
            }
        }

        LogUtil.d("onReceivedError2 errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)

        if( view == null ) {
            return
        }

        val errorCode = errorResponse?.statusCode ?: -1
        val failingUrl = request?.url.toString()

        if( view.url.equals( failingUrl ) ) {
            when ( errorCode ) {
                403, 404, 500, 503, 504 -> {
                    val msg = (context as Context).resources.getString( R.string.webview_error_http )
                    listener?.onReceivedError( view , msg.format( errorCode), failingUrl )
                }
            }
        }

        LogUtil.d("onReceivedHttpError errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)

        if( handler == null ) {
            super.onReceivedSslError(view, handler, error)
            return
        }

        if( view == null ) {
            return
        }

        val errorCode = error?.primaryError ?: SslError.SSL_INVALID
        val failingUrl = error?.url ?: ""

        listener?.onReceivedSslError( view, handler, errorCode, view.url, failingUrl )

        LogUtil.d("onReceivedSslError errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }


    // URL 타입 체크
    fun checkCatchUrlType( _url : String? ) : Int
    {
        var type : Int = DefineCode.URL_TYPE_HTTP

        if( TextUtils.isEmpty( _url ) ) {
            return type
        }

        val uri : Uri = Uri.parse( _url )
        val scheme : String = uri.scheme ?: ""
        val ext : String = _url.getFileExtension()
        val url = (_url as String).toLowerCase( Locale.KOREAN )

        if( ext.endsWith( "mp3", true ) ) {
            type = DefineCode.URL_TYPE_AUDIO
        }
        else if( ext.endsWith( "mp4", true) || ext.endsWith( "3gp", true) ) {
            type = DefineCode.URL_TYPE_VIDEO
        }
        else if( scheme.equals("tel", true) ) {
            type = DefineCode.URL_TYPE_TEL
        }
        else if( scheme.equals( "mailto", true) ) {
            type = DefineCode.URL_TYPE_MAILTO
        }
        else if( scheme.contains("market://", true) ) {
            type = DefineCode.URL_TYPE_MARKET
        }
        else if( url.startsWith( "sms:" ) || url.startsWith("smsto:" ) ) {
            type = DefineCode.URL_TYPE_SMS
        }
        else if( url.startsWith( DefineCode.INTENT_PROTOCOL ) ) {
            type = DefineCode.URL_TYPE_INTENT
        }

        return type
    }
}