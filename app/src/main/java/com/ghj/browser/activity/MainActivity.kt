package com.ghj.browser.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintJob
import android.print.PrintManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.BrowserApp
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.SiteAdapter
import com.ghj.browser.activity.adapter.SiteMode
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.activity.adapter.data.ConsoleData
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.activity.base.BaseWebViewActivity
import com.ghj.browser.activity.viewmodel.MainViewModel
import com.ghj.browser.bottomSheet.MenuBottomSheet
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.IClickListener
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.dialog.AlertDialogFragment
import com.ghj.browser.dialog.CommonDialog
import com.ghj.browser.dialog.ScriptInputDialog
import com.ghj.browser.util.*
import com.ghj.browser.webkit.JsAlertPopupData
import com.ghj.browser.webkit.JsBridge
import com.ghj.browser.webkit.JsGetMessageData
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.android.synthetic.main.webview_loading_bar.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseWebViewActivity<MainViewModel>() , View.OnClickListener , View.OnTouchListener , JsBridge.JsCallback {

    private val TAG : String = "MainActivity"

    // ui
    var actionBar : ActionBar? = null

    // toolbar
    lateinit var animationAppbarShow : Animation
    lateinit var animationAppbarHide : Animation
    lateinit var animationToolbarShow : Animation
    lateinit var animationToolbarHide : Animation
    var isAppbarShowing = false
    var isAppbarHiding = false
    var isToolbarShowing = false
    var isToolbarHiding = false


    // data
    var isEditMode : Boolean = false
    var scrollSum = 0
    var jsReturnHandler = JsReturnHandler()
    var indexSearch = DefineCode.DEFAULT_PAGE

    // full screen
    var wvCustomView : View? = null
    var wvCustomViewCallback : WebChromeClient.CustomViewCallback? = null

    // 파일업로드
    var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    var fileChooserCallbackOld: ValueCallback<Uri?>? = null

    lateinit var siteAdapter: SiteAdapter

    // 현재페이지 url
    var currentUrl : String = ""
    var isStarted = false

    // 콘솔데이터 리스트
    var consoleList : ArrayList<ConsoleData> = arrayListOf()


    // 시작페이지
    companion object {
        var WEBVIEW_LOAD_URL = ""
        var WEBVIEW_LOAD_TIME = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main )

        if( savedInstanceState != null ) {
            wv_main.restoreState( savedInstanceState )
        }

        initData()
        initLayout()
        getViewModel().addObserver(this, bookmarkObserver, historyObserver)
    }

    override fun newViewModel(): MainViewModel {
        return ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getStringExtra( DefineCode.IT_PARAM.LOAD_URL )?.let {
            loadUrl( it )
        }
    }

    override fun onResume() {
        super.onResume()

        wv_main?.resumeTimers()
        wv_main?.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        destroyWebView()
        getViewModel().removeObserver(bookmarkObserver, historyObserver)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        wv_main?.saveState( outState )
    }

    fun initData() {
        indexSearch = intent.getStringExtra( DefineCode.IT_PARAM.LOAD_URL ) ?: DefineCode.DEFAULT_PAGE
    }

    fun initLayout() {
        setSupportActionBar( appbar )
        actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled(true)  // 커스터마이징 하기 위해 필요
            it.setDisplayHomeAsUpEnabled(false)  // 뒤로가기 버튼 표시여부, 디폴트로 true만 해도 백버튼이 생김
            it.setDisplayShowTitleEnabled(false)   // 액션바에 표시되는 제목의 표시여부
            it.setDisplayShowHomeEnabled(false)   // 홈 아이콘 표시여부
        }
//        appbar.setContentInsetsAbsolute( 0, 0 )

        // 웹뷰
        wv_main?.initWebView( this ,this, true )
        wv_main?.setOnTouchListener( this )
        wv_main?.setActivity( this )

        // 앱바
        chkBookmark.setOnClickListener( this )
        btn_refersh?.setOnClickListener( this )
        layout_txt_url?.setOnClickListener( this )
        btn_delete?.setOnClickListener( this )
        edit_url?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val search = p0.toString()
                if(search.isEmpty() || search.equals(currentUrl)) {
                    siteAdapter.siteMode = SiteMode.BOOKMARK
                    notifySiteList()
                }
                else {
                    siteAdapter.siteMode = SiteMode.HISTORY
                    getViewModel().searchHistoryData(search)
                }
            }
        })
        edit_url?.setOnKeyListener { view, keyCode, keyEvent ->
            if( keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ) {
                var url = edit_url.text.toString();
                loadUrl( url )
                changeLayoutUrlEdit( false )
                true
            }
            else {
                false
            }
        }

        // 툴바
        btn_toolbar_back?.setOnClickListener( this )
        btn_toolbar_next?.setOnClickListener( this )
        btn_toolbar_home?.setOnClickListener( this )
        btn_toolbar_bookmark?.setOnClickListener( this )
        btn_toolbar_more?.setOnClickListener( this )

        // 앱바 툴바 애니메이션
        animationAppbarShow = AnimationUtils.loadAnimation( this , R.anim.anim_appbar_show )
        animationAppbarHide = AnimationUtils.loadAnimation( this , R.anim.anim_appbar_hide )
        animationToolbarShow = AnimationUtils.loadAnimation( this , R.anim.anim_toolbar_show )
        animationToolbarHide = AnimationUtils.loadAnimation( this , R.anim.anim_toolbar_hide )

        animationAppbarShow.setAnimationListener( object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) { }

            override fun onAnimationEnd(p0: Animation?) {
                isAppbarShowing = false
            }

            override fun onAnimationStart(p0: Animation?) {
                appbar_main?.visibility = View.VISIBLE
                isAppbarShowing = true
            }
        })
        animationAppbarHide.setAnimationListener( object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) { }

            override fun onAnimationEnd(p0: Animation?) {
                isAppbarHiding = false
                appbar_main?.visibility = View.GONE
            }

            override fun onAnimationStart(p0: Animation?) {
                isAppbarHiding = true
            }
        })
        animationToolbarShow.setAnimationListener( object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) { }

            override fun onAnimationEnd(p0: Animation?) {
                isToolbarShowing = false
            }

            override fun onAnimationStart(p0: Animation?) {
                toolbar_main?.visibility = View.VISIBLE
                isToolbarShowing = true
            }
        });
        animationToolbarHide.setAnimationListener( object : Animation.AnimationListener{
            override fun onAnimationRepeat(p0: Animation?) { }

            override fun onAnimationEnd(p0: Animation?) {
                isToolbarHiding = false
                toolbar_main?.visibility = View.GONE
            }

            override fun onAnimationStart(p0: Animation?) {
                isToolbarHiding = true
            }
        })

        siteAdapter = SiteAdapter(this, object : IClickListener {
            override fun onItemClick(position: Int, url: String) {
                changeLayoutUrlEdit( false )
                loadUrl( url )
            }
        })
        rvSite.adapter = siteAdapter


        // todo 테스트 하드코딩
        btn_appcall?.setOnClickListener() { view ->
            onWebMessage()
//            onWebMessageReturn()
        }
    }

    // 즐겨찾기 목록조회 옵저버
    val bookmarkObserver : Observer<ArrayList<BookmarkData>> = Observer { list: ArrayList<BookmarkData> ->
        siteAdapter.bookmarkList.clear()
        siteAdapter.bookmarkList.addAll(list)
        notifySiteList()
    }

    // 히스토리 목록조회 옵저버
    val historyObserver : Observer<ArrayList<WebSiteData>> = Observer { list: ArrayList<WebSiteData> ->
        siteAdapter.historyList.clear()
        siteAdapter.historyList.addAll(list)
        notifySiteList()
    }

    override fun onCreateAfter() {
        getViewModel().queryBookmarkData(this)
        getViewModel().queryHistoryData(this)

        loadUrl( indexSearch )
    }

    // URL 로딩시작
    override fun onPageStarted( _webView: WebView, urlType: Int, url: String ) {
        LogUtil.d("onPageStarted urlType=" + urlType + " , url=" + url )

        if( urlType == DefineCode.URL_TYPE_HTTP ) {
            isStarted = true
            consoleList.clear()

            showEditMode( false )
            txt_title?.text = url
            setUrlEditText(url)
            showWebViewLoadingBar( true , 0 )

            // 즐겨찾기 표시
            val bookmark = SQLiteService.selectBookmarkCntByUrl(this, url)
            chkBookmark.isChecked = bookmark > 0
        }
        else {
            super.onPageStarted( _webView , urlType , url )
        }
    }

    // URL 리다이렉트
    override fun shouldOverrideLoading( _webView: WebView, urlType: Int, url: String, isRedirect: Boolean ) {
        LogUtil.d("shouldOverrideLoading urlType=" + urlType + " , url=" + url + " , isRedirect=" + isRedirect )

        if( urlType == DefineCode.URL_TYPE_HTTP ) {
            txt_title?.text = url
            setUrlEditText(url)
        }
        else {
            super.shouldOverrideLoading( _webView , urlType , url , isRedirect )
        }

        changePageMoveButton()
    }

    override fun onPageFinished( _webView: WebView, url: String ) {
        LogUtil.d("onPageFinished ${url}")

        // 히스토리 데이터 입력
        _webView.copyBackForwardList().currentItem?.let {
            if( isStarted) {
                isStarted = false
                val date : String = SimpleDateFormat( "yyyyMMddHHmmss" , Locale.getDefault() ).format( Date() )
                val params: Array<String> = arrayOf( date, it.title, url, Util.bitmapToString(it.favicon) )
                SQLiteService.insertHistoryData(this, params)
                getViewModel().queryHistoryData(this)
            }
        }

        txt_title?.text = StringUtil.getUrlDoamin( url )
        setUrlEditText(url)
        showWebViewLoadingBar( false , 0 )
        changePageMoveButton()
    }

    override fun onReceivedIcon(_webView: WebView, icon: Bitmap?) {
        SQLiteService.updateHistoryFavIcon(this, arrayOf(Util.bitmapToString( icon )) )
    }


    override fun onReceivedError(_webView: WebView, errorMsg: String, url: String?) {
        LogUtil.d("onReceivedError" )

        _webView.stopLoading()
        _webView.loadUrl( DefineCode.ERROR_PAGE + "?errorMsg=" + errorMsg )

        showWebViewLoadingBar( false , 0 )
        changePageMoveButton()
    }

    override fun onReceivedSslError(_webView: WebView, handler: SslErrorHandler, errorCode: Int, url: String, failUrl: String) {
        LogUtil.d("onReceivedSslError errorCode=" + errorCode + " , url=" + url + " , failUrl=" + failUrl )

        dialog?.dismiss()

        val message = getString( R.string.webview_error_ssl )
        val buttons = arrayOf( getString( R.string.common_cancel) , getString( R.string.common_ok) )
        dialog = CommonDialog( this , 0 , message , buttons , true, TAG ){ dialog, dialogId, selected, data ->
            if( selected == DefineCode.BTN_RIGHT ) {
                handler.proceed()
            }
            else {
                handler.cancel()
            }
        }

        dialog?.show()
    }

    override fun onReceivedTitle(_webView: WebView, title: String) {
        LogUtil.d("onReceivedTitle title=" + title )
    }

    override fun onProgressChanged(_webView: WebView, newProgress: Int) {
        if( newProgress == 0 ) {
            showEditMode( false )
        }

        showWebViewLoadingBar( true , newProgress )
    }


    // show/hide progress bar
    fun showWebViewLoadingBar( isShow : Boolean , progress : Int ) {
        if( progress >= 100 ) {
            webview_progress_bar.visibility = View.GONE
            webview_progress_bar.progress = 0
            return
        }

        if( isShow ) {
            webview_progress_bar.visibility = View.VISIBLE
            if( progress >= 0 ) {
                webview_progress_bar.progress = progress
            }
        }
        else {
            webview_progress_bar.visibility = View.GONE
            webview_progress_bar.progress = 0
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            // 새로고침
            R.id.btn_refersh -> {
                wv_main?.reload()
            }
            // 에디트 모드로 변경
            R.id.layout_txt_url -> {
                changeLayoutUrlEdit( true )
            }
            // 입력박스 지우기
            R.id.btn_delete -> {
                edit_url.setText( "" )
            }
            // 뒤로가기
            R.id.btn_toolbar_back -> {
                moveToBackPage()
            }
            // 앞으로가기
            R.id.btn_toolbar_next -> {
                moveToNextPage()
            }
            // 기본페이지로가기
            R.id.btn_toolbar_home -> {
                moveToDefaultPage()
            }
            // 북마크
            R.id.btn_toolbar_bookmark -> {
                moveToBookmark()
            }
            // 설정
            R.id.btn_toolbar_more -> {
                val menuDialog = MenuBottomSheet(this) { dialog, selected ->
                    when( selected ) {
                        DefineCode.MORE_MENU_COOKIE -> {
                            moveToCookie()
                        }
                        DefineCode.MORE_MENU_PRINTER -> {
                            moveToPrinter()
                        }
                        DefineCode.MORE_MENU_STORAGE -> {
                            moveToStorage()
                        }
                        DefineCode.MORE_MENU_HISTORY -> {
                            moveToHistory()
                        }
                        DefineCode.MORE_MENU_CONSOLE -> {
                            moveToConsoleLog()
                        }
                        DefineCode.MORE_MENU_WEBVIEW_LOG -> {
                            moveToWebViewLog()
                        }
                        DefineCode.MORE_MENU_JS_EXECUTE -> {
                            showScriptInputDialog()
                        }
                        DefineCode.MORE_MENU_HTML_ELEMENT -> {
                            moveToHtmlElement()
                        }
                        DefineCode.MORE_MENU_SETTING -> {

                        }
                    }
                }
                menuDialog.show()
            }
            // 즐겨찾기
            R.id.chkBookmark -> {
                var _url = ""
                var _title = ""
                var _favIcon = ""
                wv_main.copyBackForwardList().currentItem?.let {
                    _url = it.url
                    _title = it.title
                    _favIcon = Util.bitmapToString(it.favicon)
                }

                if( chkBookmark.isChecked ) {
                    SQLiteService.insertBookmarkData(this, arrayOf(_url, _title, _favIcon))
                }
                else {
                    SQLiteService.deleteBookmarkData(this, arrayListOf(_url))
                }
                getViewModel().queryBookmarkData(this)
            }
        }
    }

    // 쿠키설정 화면으로 이동
    fun moveToCookie() {
        val url = wv_main?.url

        val intent : Intent = Intent( this , CookieActivity::class.java )
        intent.putExtra( DefineCode.IT_PARAM_COOKIE_URL , url )
        startActivity( intent )
    }

    // 인쇄화면으로 이동
    fun moveToPrinter() {
        if( wv_main == null ) {
            return
        }

        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
            return
        }

        val pm : PrintManager? = getSystemService( Context.PRINT_SERVICE ) as? PrintManager
        pm?.let {
            val documentAdapter : PrintDocumentAdapter =
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    val documentName = if( !TextUtils.isEmpty( wv_main?.title ) ) wv_main.title ?: "" else getString( R.string.app_name )
                    wv_main.createPrintDocumentAdapter( documentName )
                } else {
                    wv_main.createPrintDocumentAdapter()
                }

            val printJobName : String = getString( R.string.app_name )
            val printJob : PrintJob = it.print( printJobName , documentAdapter , PrintAttributes.Builder().build() )
        }
    }

    // 스토리지 페이지로 이동
    fun moveToStorage() {
        if( Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT ) {
            wv_main?.evaluateJavascript("javascript:JSON.stringify(localStorage);") { localS: String ->
                val strLocal = localS
                    .replace("\\\"", "\"")
                    .replace("\\\\\"", "\"")
                    .replace("\"{", "{")
                    .replace("}\"","}")
                    .replace("\"[", "[")
                    .replace("]\"", "]")

                wv_main?.evaluateJavascript("javascript:JSON.stringify(sessionStorage);") { sessionS: String ->
                    val strSession = sessionS
                        .replace("\\\"", "\"")
                        .replace("\\\\\"", "\"")
                        .replace("\"{", "{")
                        .replace("}\"","}")
                        .replace("\"[", "[")
                        .replace("]\"", "]")

                    val intent = Intent(this, StorageActivity::class.java)
                    intent.putExtra(DefineCode.IT_PARAM.LOCAL_STORAGE, strLocal)
                    intent.putExtra(DefineCode.IT_PARAM.SESSION_STORAGE, strSession)
                    startActivity(intent)
                }
            }
        }
    }

    // 데스크탑 모드 <-> 모바일 모드 토클
    fun chnagePcMobileMode() {
        wv_main?.let {
            if( BrowserApp.isMobile ) {
                val userAgent = it.settings.userAgentString.replace( "Android" , "droidA" ).replace( "Mobile" , "obileM" )
                it.settings.userAgentString = userAgent

                val url = it.url?.replace( Regex("^https:\\/\\/m\\.") , "https://www.")?.replace( Regex( "^http:\\/\\/m\\.") , "http://www.")
                loadUrl( url )
            }
            else {
                val userAgent = it.settings.userAgentString.replace( "droidA" , "Android" ).replace( "obileM" , "Mobile" )
                it.settings.userAgentString = userAgent

                it.reload()
            }

            BrowserApp.isMobile = !BrowserApp.isMobile
        }
    }

    // 방문기록 페이지로 이동
    fun moveToHistory() {
        val intent : Intent = Intent( this , HistoryActivity::class.java )
        startActivityForResult( intent , DefineCode.ACT_REQ_ID.HISTORY )
    }

    // 콘솔로그 페이지로 이동
    fun moveToConsoleLog() {
        val intent : Intent = Intent(this, ConsoleActivity::class.java)
        intent.putExtra( DefineCode.IT_PARAM_CONSOLE, consoleList )
        startActivity(intent)
    }

    // 웹뷰로그 페이지로 이동
    fun moveToWebViewLog() {
        val intent : Intent = Intent(this, WebViewLogActivity::class.java)
        startActivity(intent)
    }

    // 자바스크립트 입력 다이얼로그
    fun showScriptInputDialog() {
        val dialog = ScriptInputDialog(this) {dialog: Dialog, script: String ->
            if( Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT ) {
                wv_main.evaluateJavascript(script) {result: String ->
                    CommonDialog( this , 0 , result , null , true, TAG, null).show()
                }
            }
            else {
                wv_main.loadUrl("javascript:${script}")
            }
        }
        dialog.show()
    }

    // HTML 요소 페이지로 이동
    fun moveToHtmlElement() {
        if( Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT ) {
            wv_main.evaluateJavascript("document.documentElement.outerHTML;") {result: String ->
                PrefUtil.getInstance(this).put(DefineCode.PREF_KEY.HTML_ELEMENT, result)
                val intent : Intent = Intent(this, HtmlElementActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        when ( p0?.id ) {
            // 웹뷰 클릭시 키보드 내리고 텍스트모드로 변경
            R.id.wv_main -> {
                if( isEditMode ) {
                    if( p1?.action == MotionEvent.ACTION_DOWN || p1?.action == MotionEvent.ACTION_UP || p1?.action == MotionEvent.ACTION_CANCEL ) {
                        changeLayoutUrlEdit( false )
                    }
                }
            }
        }
        return false
    }

    // show / hide editmode
    fun showEditMode( isEdit: Boolean ) {
        if( isEdit ) {
            layout_url_edit_mode.visibility = View.VISIBLE
            layoutSite.visibility = View.VISIBLE
            layout_url_txt_mode.visibility = View.GONE
        }
        else {
            layout_url_edit_mode.visibility = View.GONE
            layoutSite.visibility = View.GONE
            layout_url_txt_mode.visibility = View.VISIBLE
        }
        isEditMode = isEdit
    }

    // URL 입력하도록 변경
    fun changeLayoutUrlEdit( isEdit: Boolean) {
        showEditMode(isEdit)

        siteAdapter.siteMode = SiteMode.BOOKMARK
        if( isEdit ) {
            edit_url?.isFocusableInTouchMode = true
            edit_url?.requestFocus()
            showKeyboard( edit_url , true )
            notifySiteList()
        }
        else {
            edit_url?.isFocusableInTouchMode = false
            edit_url?.clearFocus()

            setUrlEditText( wv_main?.url )
            showKeyboard( edit_url , false )
        }
        isEditMode = isEdit
    }

    // show / hide keyboard
    fun showKeyboard( edit_text : EditText , isShow : Boolean ) {
        val imm : InputMethodManager? = getSystemService( Context.INPUT_METHOD_SERVICE ) as? InputMethodManager
        if( isShow ) {
            edit_text.requestFocus()
            imm?.let {
                it.showSoftInput( edit_text , InputMethodManager.SHOW_IMPLICIT )
            }
        }
        else {
            edit_text.clearFocus()
            imm?.let {
                it.hideSoftInputFromWindow( edit_text.windowToken , 0 )
            }
        }
    }

    // URL입력상자
    fun setUrlEditText(url: String?) {
        if(url != null) {
            currentUrl = url
            edit_url?.setText(url)
        }
    }

    // URL입력 즐겨찾기/히스토리 목록
    fun notifySiteList() {
        if(isEditMode) {
            txtSite.text = if(siteAdapter.siteMode == SiteMode.BOOKMARK) {"즐겨찾기"} else {"방문한 페이지"}
            siteAdapter.notifyDataSetChanged()
        }
    }

    // 웹페이지 로딩
    fun loadUrl( _url : String? ) {
        if( TextUtils.isEmpty( _url ) ) {
            return
        }

        var url = _url as String
        if( !url.contains( "://" ) ) {
            url = "https://" + url
        }

        wv_main?.loadUrl( url )
    }

    override fun onBackPressed() {
        if(isEditMode) {
            changeLayoutUrlEdit(false)
            return
        }

        if( wv_main?.canGoBack() ?: false ) {
            moveToBackPage()
        }
        else {
            finish()
        }
    }

    // 웹뷰 페이지 뒤로가기
    fun moveToBackPage() {
        val step = getWillMovePageStep( wv_main, true )

        wv_main?.let {
            if( step == null || step >= 0 ) {
                finish()
            }
            else {
                if( step == -1 ) {
                    if( it.canGoBack() ) {
                        it.goBack()
                    }
                    else {
                        finish()
                    }
                }
                else {
                    if( it.canGoBackOrForward( step ) ) {
                        it.goBackOrForward( step )
                    }
                    else {
                        finish()
                    }
                }
            }
        }
    }

    // 웹뷰 페이지 앞으로가기
    fun moveToNextPage() {
        val step = getWillMovePageStep( wv_main, false )

        wv_main?.let {
            if( step == null || step <= 0 ) {
                it.reload()
            }
            else {
                if( step == 1 ) {
                    if( it.canGoForward() ) {
                        it.goForward()
                    }
                }
                else {
                    if( it.canGoBackOrForward( step ) ) {
                        it.goBackOrForward( step )
                    }
                }
            }
        }
    }

    // 웹뷰 앞으로/뒤로가기 버튼 UI
    fun changePageMoveButton() {
        wv_main?.let {
            // 앞으로가기 버튼
            if( it.canGoForward() ) {
                btn_toolbar_next?.visibility = View.VISIBLE
            }
            else {
                btn_toolbar_next?.visibility = View.INVISIBLE
            }
        }
    }

    // 웹뷰 기본페이지로가기
    fun moveToDefaultPage() {
        val defaultPage = DefineCode.DEFAULT_PAGE
        loadUrl( defaultPage )
    }

    // 즐겨찾기 화면으로 이동
    fun moveToBookmark() {
        val intent: Intent = Intent(this, BookmarkActivity::class.java)
        startActivityForResult(intent, DefineCode.ACT_REQ_ID.BOOKMARK)
    }

    // 웹뷰 스크롤시 타이툴바 툴바 show/hide
    override fun onScrollChanged(t: Int, oldt: Int) {
        if( t == 0 ) {
            if( appbar_main?.visibility != View.VISIBLE ) {
                isAppbarShowing = true
                isAppbarHiding = false
                appbar_main?.startAnimation( animationAppbarShow )
            }
            if( toolbar_main?.visibility != View.VISIBLE ) {
                isToolbarShowing = true
                isToolbarHiding = false
                toolbar_main?.startAnimation( animationToolbarShow )
            }
            return
        }
        val gap = t - oldt  // > 0 : 아래로 스크롤 , < 0 : 위로 스크롤

        if( gap == appbar_main.height || gap == -appbar_main.height || gap > -10 && scrollSum < 0 || gap < 10 && scrollSum > 0 ) {
            scrollSum = 0
            return
        }

        scrollSum += gap

        if( scrollSum == gap || Math.abs( scrollSum-gap) < 20 ) {
            return
        }

        // 아래로 스크롤하면 숨기고
        if( scrollSum > appbar_main.height ) {
            if( appbar_main?.visibility == View.VISIBLE && !isAppbarHiding ) {
                isAppbarShowing = false
                isAppbarHiding = true
                appbar_main?.startAnimation( animationAppbarHide )
            }
            if( toolbar_main?.visibility == View.VISIBLE && !isToolbarHiding ) {
                isToolbarShowing = false
                isToolbarHiding = true
                toolbar_main?.startAnimation( animationToolbarHide )
            }
        }
        // 위로 스크롤하면 보인다
        else if( scrollSum < -appbar_main.height) {
            if( appbar_main?.visibility != View.VISIBLE && !isAppbarShowing ) {
                isAppbarShowing = true
                isAppbarHiding = false
                appbar_main?.startAnimation( animationAppbarShow )
            }
            if( toolbar_main?.visibility != View.VISIBLE && !isToolbarShowing ) {
                isToolbarShowing = true
                isToolbarHiding = false
                toolbar_main?.startAnimation( animationToolbarShow )
            }
        }
    }

    override fun onRequestFromJs(requestId: Int, vararg params: String?) {
        when (requestId) {
            DefineCode.JS_ALERT_POPUP -> {
                onJsAlertPopup( *params )
            }
        }
    }

    fun onJsAlertPopup( vararg params : String? ) {
        if( params.isEmpty() ) {
            return
        }

        val dto = JsonUtil.parseJson( params[0] , JsAlertPopupData::class.java )
        dto?.let {
            val dialog = AlertDialogFragment.newInstance( 0 , it.title , it.message , false , null, object : AlertDialogFragment.AlertDialogFragmentInterface {
                override fun onClickListener( dialog: AlertDialogFragment, requestId: Int, selected: Int ) {

                }
            })
            dialog.show( supportFragmentManager , TAG )
        }
    }

    // 앱 -> 웹에 메시지 전달
    fun onWebMessage() {
        val dto = JsGetMessageData( "알림" , "App에서 전달한 메시지 !!!!!" )
        val json = JsonUtil.dtoToJsonString( dto )
        callJsFunction( wv_main , "appGetMessage" , arrayOf(json) )
    }

    // 앱 -> 웹에 메시지 전달 후 리턴값 받음
    fun onWebMessageReturn() {
        val dto = JsGetMessageData( "알림" , "App에서 전달한 메시지 !!!!!" )
        val json = JsonUtil.dtoToJsonString( dto )
        callJsFunction( wv_main , "appGetMessageReturn" , arrayOf(json) , jsReturnHandler , DefineCode.HDL_WHAT_JS_GET_MESSAGE )
    }

    // Native -> Js 에서 값 리턴
    inner class JsReturnHandler : Handler( Looper.getMainLooper() ) {

        override fun handleMessage(msg: Message) {

            // appGetMessage : 반환딘값 alert
            if( msg.what == DefineCode.HDL_WHAT_JS_GET_MESSAGE ) {
                val message = msg.data?.getString( DefineCode.HDL_PARAM_WV_BRIDGE_RTN , "" )
                val dialog = AlertDialogFragment.newInstance( 0 , "" , message , false , null, null )
                dialog.show( supportFragmentManager , TAG)
            }
        }
    }

    override fun onShowCustomView(_view: View, callback: WebChromeClient.CustomViewCallback?) {
        if( custom_view_container.visibility == View.VISIBLE ) {
            return
        }
        wvCustomView = _view

        custom_view_container.addView( wvCustomView )
        custom_view_container.visibility = View.VISIBLE
        wvCustomViewCallback = callback
    }

    override fun onHideCustomView() {
        custom_view_container.visibility = View.GONE
        custom_view_container.removeView( wvCustomView )
        wvCustomViewCallback?.onCustomViewHidden()
    }

    // 웹뷰 show/hide
    fun destroyWebView() {
        wv_main?.clearCache(true)
        wv_main?.clearView()
        wv_main?.removeAllViews()
//            wv_main?.clearHistory()   // onPageFinished() 에서만 동작
        wv_main?.clearFocus()
        wv_main?.clearFormData()
        wv_main?.clearSslPreferences()
        wv_main?.onPause()
        wv_main?.destroy()
    }

    // 파일업로드 - 롤리팝이후
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(_webView: WebView, callback: ValueCallback<Array<Uri>>, params: WebChromeClient.FileChooserParams?) {
        try {
            if (fileChooserCallback != null) {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = null
            }

            fileChooserCallback = callback

            val intent = params?.createIntent();
            startActivityForResult(intent, DefineCode.ACT_REQ_ID.IT_FILE_SELECT)
        }
        catch ( e: Exception ) {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("*/*")
                startActivityForResult(Intent.createChooser(intent, getString(R.string.file_select_title)), DefineCode.ACT_REQ_ID.IT_FILE_SELECT)
            }
            catch ( e1: Exception ) { }
        }
    }

    // 파일업로드 - 롤리팝이전
    override fun onShowFileChooser(callback: ValueCallback<Uri?>) {
        try {
            if( fileChooserCallbackOld != null ) {
                fileChooserCallbackOld?.onReceiveValue(null)
                fileChooserCallbackOld = null
            }

            fileChooserCallbackOld = callback

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.setType("*/*")
            startActivityForResult(Intent.createChooser(intent, getString(R.string.file_select_title)), DefineCode.ACT_REQ_ID.IT_FILE_SELECT)
        }
        catch ( e: Exception ) { }
    }

    override fun onConsoleMessage(log: String) {
        val date : String = SimpleDateFormat( "yyyyMMddHHmmss" , Locale.getDefault() ).format( Date() )
        consoleList.add(ConsoleData(date, log))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            // 파일선택 후 콜백
            DefineCode.ACT_REQ_ID.IT_FILE_SELECT -> {
                if( resultCode == Activity.RESULT_OK ) {
                    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                        if( data != null ) {
                            fileChooserCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
                            fileChooserCallback = null
                        }
                    }
                    else {
                        if( data != null ) {
                            fileChooserCallbackOld?.onReceiveValue(data.data)
                            fileChooserCallbackOld = null
                        }
                    }
                }
            }

            // 히스토리 액티비티
            DefineCode.ACT_REQ_ID.HISTORY -> {
                if( resultCode == Activity.RESULT_OK ) {
                    // 클릭한 사이트로 이동
                    val url = data?.getStringExtra( DefineCode.IT_PARAM.HISTORY_URL )
                    if( !TextUtils.isEmpty(url) ) {
                        loadUrl( url )
                    }
                }
            }

            // 즐겨찾기 액티비티
            DefineCode.ACT_REQ_ID.BOOKMARK -> {
                if( resultCode == Activity.RESULT_OK ) {

                    val isChanged = data?.getBooleanExtra( DefineCode.IT_PARAM.IS_CHANGED, false ) ?: false
                    if( isChanged ) {
                        getViewModel().queryBookmarkData(this)
                        wv_main.copyBackForwardList().currentItem?.let {
                            val bookmark = SQLiteService.selectBookmarkCntByUrl(this, it.url)
                            chkBookmark.isChecked = bookmark > 0
                        }
                    }
                }
            }
        }
    }

    override fun onLoadResource(_webView: WebView, url: String) {
        // 확대/축소 안되는 페이지에 viewport 변경
        wv_main.loadUrl("javascript:document.getElementsByName(\"viewport\")[0].setAttribute(\"content\", \"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=5.0, user-scalable=yes\");");
    }

}
