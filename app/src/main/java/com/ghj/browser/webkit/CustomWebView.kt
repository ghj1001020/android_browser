package com.ghj.browser.webkit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.webkit.*
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.PermissionUtil
import com.ghj.browser.util.StringUtil
import java.io.File
import java.lang.Exception
import java.net.URLDecoder


class CustomWebView : WebView {

    private val TAG = "CustomWebView"


    private var mContext : Context? = null
    private var listener : OnWebViewListener? = null
    private var customWebChromeClient : CustomWebChromeClient? = null


    // 파일다운로드 정보
    var fileDownloadUrl : String? = null
    var fileDownloadFilename : String? = null


    constructor( context: Context ) : this(context, null){
        mContext = context
    }
    constructor( context: Context , attrs : AttributeSet? ) : this(context, attrs, android.R.attr.webViewStyle) {
        mContext = context
    }

    constructor( context: Context , attrs: AttributeSet? , defStyleAttr : Int ) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }

    // 웹뷰 초기화
    @SuppressLint("JavascriptInterface")
    fun initWebView(listener: OnWebViewListener, callback: JsBridge.JsCallback, hasWindowOpen : Boolean ) {
        clearHistory()
        clearCache( true )

        this.listener = listener

        customWebChromeClient = CustomWebChromeClient( mContext , this.listener )
        webChromeClient = customWebChromeClient
        webViewClient = CustomWebViewClient( mContext , this.listener )
        initWebViewSettings( hasWindowOpen )

        // 브릿지 함수
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            addJavascriptInterface( JsBridge( callback ) , "browserApp" )
        }

        // 웹뷰에 포커스를 받을 수 있도록 설정
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            focusable = View.FOCUSABLE
        }
        requestFocus( View.FOCUS_DOWN )
        setFocusable( true )
        isFocusableInTouchMode = true   // 터치 모드에서 뷰에 초점을 맞출 수 있는지 여부

        // 상하좌우 스크롤
        if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.M ) {
            setVerticalScrollbarOverlay( true ) // 세로막대 스크롤에 오버레이 스타일 여부
        }
        isHorizontalScrollBarEnabled = true
        isVerticalScrollBarEnabled = true
        scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        isScrollbarFadingEnabled = false

        // 쿠키 허용
        val cookieManager : CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie( true )
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            cookieManager.setAcceptThirdPartyCookies( this , true )
        }

        setInitialScale( 1 )
        setNetworkAvailable( true )
        isDrawingCacheEnabled = true    // 하드웨어 가속 렌더링이 도입되며 deprecate
        clipChildren = true // 경계에 넘어감 자식뷰는 잘림

        // 킷캣 이상에서는 하드웨어 가속이 성능이 좋음 - NestedScrollView와 충돌 (라이브러리 버그)
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            setLayerType( View.LAYER_TYPE_HARDWARE, null )
        }
        else {
            setLayerType( View.LAYER_TYPE_SOFTWARE, null )
        }

        // 디버깅
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            setWebContentsDebuggingEnabled( true )
        }

        // 다운로드
        setDownloadListener( DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            var filename = ""

            if( !TextUtils.isEmpty( contentDisposition ) ) {
                try {
                    filename = URLUtil.guessFileName( url , contentDisposition , mimetype )
                    filename = URLDecoder.decode( filename , "UTF-8" )
                }
                catch ( e : Exception ) {
                    LogUtil.e("err=" + e.message )
                }
            }

            fileDownload( url , filename )
        } )
    }

    // 웹뷰 설정
    fun initWebViewSettings( hasWindowOpen : Boolean) {
        val settings : WebSettings = getSettings()

        // [20150319] 삼성 키패드 쿼티 이슈 - 자동완성 OFF
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O ) {
            settings.saveFormData = false   // 웹뷰가 양식 데이터를 저장할지 여부 (오레오 이후로 자동 완성 서비스에 저장)
        }

        settings.setNeedInitialFocus( true )    // 웹뷰가 requestFocus 호출시 포커스를 갖도록 설정해야는지 여부
        settings.javaScriptEnabled = true

        // 윈도우 오픈
        if( hasWindowOpen ) {
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows( true )
        }
        else {
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportMultipleWindows( false )
        }

        // 네트워크 이미지 리소스 로드 여부
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        // 시스템 파일 접근 허용여부
        settings.allowFileAccess = true

        // 파일 스키마 URL의 컨텍스트에서 실행중인 JavaScript가 다른 파일 스키마 URL의 컨텐츠에 액세스 할 수 있는지 여부
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
        }

        // HTTPS > HTTP 전송시 내장 브라우저에서 block 시켜 데이터 전송이 안되는 문제(롤리팝(5.0) 이상)
        // [blocked] The page at 'https://xxx' was loaded over HTTPS, but ran insecure content from http://xxx.css': this content should also be loaded over HTTPS.
        // 혼합된 컨텐츠와 서드파티 쿠키가 설정에 따라 Webview 에서 Block 시키는 게 기본으로 변경
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        // 렌더링 우선순위
        if( Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            settings.setRenderPriority( WebSettings.RenderPriority.HIGH )
        }

        // 웹페이지를 화면 폭에 맞추기
        settings.layoutAlgorithm =  WebSettings.LayoutAlgorithm.SINGLE_COLUMN   // deprecated
        settings.loadWithOverviewMode = true

        // 확대 축소 지원
        settings.builtInZoomControls = true
        settings.setSupportZoom( true )
        settings.displayZoomControls = false    // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정

        // HTML "viewport" 메타태그 지원 여부
        settings.useWideViewPort = true

        // Local Storage
        settings.databaseEnabled = true // 데이터베이스 스토리지 API 사용 여부를 설정
        settings.domStorageEnabled = true   // DOM 스토리지 API 사용 여부를 설정
        settings.setAppCacheEnabled( true ) // Application Caches API 사용 여부를 설정 (setAppCachePath (String)에 유효한 데이터베이스 경로 제공필요)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE  // 캐시 사용안함 (네트워크에서 로드)
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            settings.setAppCacheMaxSize( Long.MAX_VALUE )   // 응용 프로그램 캐시 최대 크기 설정
        }

        // GPS 사용여부
        settings.setGeolocationEnabled( true )

        //웹뷰에서 시스템 텍스트 크기를 무시
        settings.textZoom = 100

        // HTML 페이지 텍스트 인코딩
        settings.defaultTextEncodingName = "UTF-8"
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        listener?.onScrollChanged( t, oldt )
    }

    // 웹뷰에 액티비티 전달
    fun setActivity( activity : Activity )
    {
        customWebChromeClient?.setActivity( activity )
    }

    // 위치권한 결과
    fun onLocationPermissionResult( isGranted : Boolean )
    {
        customWebChromeClient?.onLocationPermissionResult( isGranted )
    }

    // 파일 다운로드
    fun fileDownload( url : String , _filename : String? ) {
        if( mContext == null ) {
            return
        }

        val perm = PermissionUtil.checkPermissions( mContext as Context , PermissionUtil.WRITE_EXTERNAL_PERMISSION )
        // 저장 권한없을 경우 권한 요청
        if( perm != PermissionUtil.PERMISSION_GRANTED ) {
            fileDownloadUrl = url
            fileDownloadFilename = _filename

            listener?.onRequestPermissions( DefineCode.PERM_ID_WEBVIEW_WRITE_EXTERNAL_STORAGE , perm , PermissionUtil.WRITE_EXTERNAL_PERMISSION )
        }
        // 파일다운로드
        else {
            try {
                // 다운받을 파일명
                val filename : String = if( TextUtils.isEmpty( _filename ) ) StringUtil.getFilenameFromUrl( url ) else _filename!!
                val request : DownloadManager.Request = DownloadManager.Request( Uri.parse( url ) )

                val cookies = CookieManager.getInstance().getCookie( url )
                request.addRequestHeader( "cookie" , cookies )

                request.setTitle( filename )
                request.setDescription( "File Download")
                if( Build.VERSION_CODES.Q > Build.VERSION.SDK_INT ) {
                    request.allowScanningByMediaScanner()
                }
                request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED )
                request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS , filename )

                val dm : DownloadManager? = (mContext as Context).getSystemService( Context.DOWNLOAD_SERVICE ) as DownloadManager?
                dm?.enqueue( request )
            }
            catch ( e : Exception ) {
                e.printStackTrace()
            }
        }
    }

    // 저장권한 결과
    fun onWriteExternalStoragePermissionResult( isGranted : Boolean) {
        if( isGranted ) {
            fileDownloadUrl?.let {
                fileDownload( it , fileDownloadFilename )
            }
        }
    }
}