package com.ghj.browser.webkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView


class CustomWebView : WebView {

    private val TAG = "CustomWebView"


    private var mContext : Context? = null
    private var listener : OnWebViewListener? = null


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
    fun initWebView(listener: OnWebViewListener, hasWindowOpen : Boolean ) {
        clearHistory()
        clearCache( true )

        this.listener = listener

        webChromeClient = CustomWebChromeClient( mContext , this.listener )
        webViewClient = CustomWebViewClient( mContext , this.listener )
        initWebViewSettings( hasWindowOpen )

        // 브릿지 함수
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            addJavascriptInterface( JsBridge() , "browserApp" )
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
        isScrollbarFadingEnabled = true

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
        setDownloadListener( null )
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
        settings.builtInZoomControls = false    // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정
        settings.setSupportZoom( true )
        settings.displayZoomControls = true

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
}