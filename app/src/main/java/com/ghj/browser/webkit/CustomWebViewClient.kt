package com.ghj.browser.webkit

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.webkit.*
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.getFileExtension
import java.util.*

class CustomWebViewClient : WebViewClient {

    private val TAG : String = "CustomWebViewClient"

    private var context : Context? = null
    private var listener : OnWebViewListener? = null


    constructor( context: Context? , listener: OnWebViewListener? ) : super() {
        this.context = context
        this.listener = listener
    }


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        if( view == null || TextUtils.isEmpty( url ) ) {
            return
        }

        // url 타입 체크
        val type = checkCatchUrlType( url )
        listener?.onPageStarted( view, type, url!! )

        LogUtil.d( TAG , "onPageStarted type=" + type + " , url=" + url )
    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        if( view == null || TextUtils.isEmpty( url ) ) {
            return false
        }

        // url 타입 체크
        val type = checkCatchUrlType( url )
        if( listener != null ) {
            listener?.shouldOverrideLoading( view, type, url as String, false )

            LogUtil.d( TAG , "shouldOverrideUrlLoading1 type=" + type + " , url=" + url )
            return false
        }

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
        if( listener != null ) {
            listener?.shouldOverrideLoading( view, type, url, request.isRedirect )  // isRedirect : 요청이 서버측 리다이렉션의 결과인지 여부

            LogUtil.d( TAG , "shouldOverrideUrlLoading2 type=" + type + " , url=" + url + " , isRedirect=" + request.isRedirect )
        }

        return type != DefineCode.URL_TYPE_HTTP
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if( view == null || TextUtils.isEmpty( url ) ) {
            return
        }

        listener?.onPageFinished( view, url!! )

        LogUtil.d( TAG , "onPageFinished url=" + url )
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

//        LogUtil.d( TAG , "onLoadResource url=" + url )
    }

    @SuppressWarnings("deprecation")
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            return
        }

        if( view == null ) {
            return
        }

        listener?.onReceivedError( view, errorCode, view.url, failingUrl ?: "" )

        LogUtil.d( TAG , "onReceivedError1 errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)

        if( view == null ) {
            return
        }

        val errorCode = error?.errorCode ?: WebViewClient.ERROR_UNKNOWN
        val failingUrl = request?.url.toString()
        listener?.onReceivedError( view, errorCode, view.url, failingUrl )

        LogUtil.d( TAG , "onReceivedError2 errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)

        if( view == null ) {
            return
        }

        val errorCode = errorResponse?.statusCode ?: -1
        val failingUrl = request?.url.toString()
        listener?.onReceivedHttpError( view, errorCode, view.url, failingUrl )

        LogUtil.d( TAG , "onReceivedHttpError errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
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

        LogUtil.d( TAG , "onReceivedSslError errorCode=" + errorCode + " , url=" + view.url + " , failingUrl=" + failingUrl )
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
        else if( url.startsWith( "sms" ) ) {
            type = DefineCode.URL_TYPE_SMS
        }
        else if( url.startsWith( DefineCode.INTENT_PROTOCOL ) ) {
            type = DefineCode.URL_TYPE_INTENT
        }

        return type
    }
}