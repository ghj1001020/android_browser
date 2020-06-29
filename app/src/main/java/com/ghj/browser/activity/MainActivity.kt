package com.ghj.browser.activity

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.webkit.SslErrorHandler
import android.webkit.WebView
import androidx.appcompat.app.ActionBar
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseWebViewActivity
import com.ghj.browser.common.DefinePage
import com.ghj.browser.util.LogUtil
import com.ghj.browser.util.StringUtil
import com.ghj.browser.webkit.OnWebViewListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.appbar_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import kotlinx.android.synthetic.main.webview_loading_bar.*

class MainActivity : BaseWebViewActivity() , OnWebViewListener , View.OnClickListener , View.OnTouchListener {

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main )

        LogUtil.d( TAG , "onCreate " + savedInstanceState?.toString())
        if( savedInstanceState != null ) {
            wv_main.restoreState( savedInstanceState )
        }

        initLayout()
    }

    override fun onResume() {
        super.onResume()

        wv_main?.resumeTimers()
        wv_main?.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        wv_main?.saveState( outState )
    }

    fun initLayout() {
        setSupportActionBar( appbar )
        actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled( true )  // 커스터마이징 하기 위해 필요
            it.setDisplayHomeAsUpEnabled( false )  // 뒤로가기 버튼 표시여부, 디폴트로 true만 해도 백버튼이 생김
            it.setDisplayShowTitleEnabled( false )   // 액션바에 표시되는 제목의 표시여부
            it.setDisplayShowHomeEnabled( false )   // 홈 아이콘 표시여부
        }

        // 웹뷰
        wv_main?.initWebView( this ,false )
        wv_main?.setOnTouchListener( this )

        // 앱바
        btn_refersh?.setOnClickListener( this )
        layout_txt_url?.setOnClickListener( this )
        btn_delete?.setOnClickListener( this )
        edit_url?.setOnKeyListener { view, keyCode, keyEvent ->
            if( keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER ) {
                loadUrl()
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
    }

    override fun onCreateAfter() {
        moveToDefaultPage()
        // 스크롤
//        wv_main?.loadUrl( "https://1boon.daum.net/theable/novel3" )
    }

    override fun onPageStarted( _webView: WebView, urlType: Int, url: String ) {
        LogUtil.d( TAG , "onPageStarted urlType=" + urlType + " , url=" + url )

        showEditMode( true )

        txt_title?.text = url
        edit_url?.setText( url )
        showWebViewLoadingBar( true , 0 )
    }

    override fun shouldOverrideLoading( _webView: WebView, urlType: Int, url: String, isRedirect: Boolean ) {
        LogUtil.d( TAG , "shouldOverrideLoading urlType=" + urlType + " , url=" + url + " , isRedirect=" + isRedirect )

        txt_title?.text = url
        edit_url?.setText( url )
        changePageMoveButton()
    }

    override fun onPageFinished( _webView: WebView, url: String ) {
        LogUtil.d( TAG , "onPageFinished url=" + url )

        showEditMode( false )

        txt_title?.text = StringUtil.getUrlDoamin( url )
        edit_url?.setText( url )
        showWebViewLoadingBar( false , 0 )
        changePageMoveButton()
    }

    override fun onReceivedError(_webView: WebView, errorCode: Int, url: String, failUrl: String) {
        LogUtil.d( TAG , "onReceivedError errorCode=" + errorCode + " , url=" + url + " , failUrl=" + failUrl )
    }

    override fun onReceivedHttpError(_webView: WebView, errorCode: Int, url: String, failUrl: String) {
        LogUtil.d( TAG , "onReceivedHttpError errorCode=" + errorCode + " , url=" + url + " , failUrl=" + failUrl )
    }

    override fun onReceivedSslError(_webView: WebView, handler: SslErrorHandler, errorCode: Int, url: String, failUrl: String) {
        LogUtil.d( TAG , "onReceivedSslError errorCode=" + errorCode + " , url=" + url + " , failUrl=" + failUrl )
    }

    override fun onReceivedTitle(_webView: WebView, title: String) {
        LogUtil.d( TAG , "onReceivedTitle title=" + title )
    }

    override fun onProgressChanged(_webView: WebView, newProgress: Int) {
        LogUtil.d( TAG , "onProgressChanged newProgress=" + newProgress )

        if( newProgress >= 100 ) {
            showEditMode( false )
        }
        else {
            showEditMode( true )
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

            }
            // 설정
            R.id.btn_toolbar_more -> {

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
            layout_url_txt_mode.visibility = View.GONE
        }
        else {
            layout_url_edit_mode.visibility = View.GONE
            layout_url_txt_mode.visibility = View.VISIBLE
        }
        isEditMode = isEdit
    }

    // URL 입력하도록 변경
    fun changeLayoutUrlEdit( isEdit: Boolean) {
        if( isEdit ) {
            layout_url_edit_mode.visibility = View.VISIBLE
            layout_url_txt_mode.visibility = View.GONE

            edit_url?.isFocusableInTouchMode = true
            edit_url?.requestFocus()
            showKeyboard( true )
        }
        else {
            layout_url_edit_mode.visibility = View.GONE
            layout_url_txt_mode.visibility = View.VISIBLE

            edit_url?.isFocusableInTouchMode = false
            edit_url?.clearFocus()
            edit_url?.setText( wv_main?.url )
            showKeyboard( false )
        }
        isEditMode = isEdit
    }

    // show / hide keyboard
    fun showKeyboard( isShow : Boolean ) {
        val imm : InputMethodManager? = getSystemService( Context.INPUT_METHOD_SERVICE ) as? InputMethodManager
        if( isShow ) {
            imm?.let {
                it.showSoftInput( edit_url , InputMethodManager.SHOW_IMPLICIT )
            }
        }
        else {
            imm?.let {
                it.hideSoftInputFromWindow( edit_url?.windowToken , 0 )
            }
        }
    }

    // 웹페이지 로딩
    fun loadUrl() {
        var url = edit_url.text.toString();
        if( !url.contains( "://" ) ) {
            url = "https://" + url
        }

        wv_main?.loadUrl( url )
    }

    override fun onBackPressed() {
        if( wv_main?.canGoBack() ?: false ) {
            wv_main?.goBack()
        }
        else {
            finish()
        }
    }

    // 웹뷰 페이지 뒤로가기
    fun moveToBackPage() {
        wv_main?.let {
            if( it.canGoBack() ) {
                it.goBack()
            }
        }
    }

    // 웹뷰 페이지 앞으로가기
    fun moveToNextPage() {
        wv_main?.let {
            if( it.canGoForward() ) {
                it.goForward()
            }
        }
    }

    // 웹뷰 앞으로/뒤로가기 버튼 UI
    fun changePageMoveButton() {
        wv_main?.let {
            // 뒤로가기 버튼
            if( it.canGoBack() ) {
                btn_toolbar_back?.visibility = View.VISIBLE
            }
            else {
                btn_toolbar_back?.visibility = View.INVISIBLE
            }

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
        val defaultPage = DefinePage.DEFAULT_PAGE

        wv_main?.loadUrl( defaultPage )
    }

    // 웹뷰 스크롤시 타이툴바 툴바 show/hide
    override fun onScrollChanged(t: Int, oldt: Int) {
        if( t == 0 ) {
            if( appbar_main?.visibility != View.VISIBLE ) {
                isAppbarShowing = false
                isAppbarHiding = false
                appbar_main?.startAnimation( animationAppbarShow )
            }
            if( toolbar_main?.visibility != View.VISIBLE ) {
                isToolbarShowing = false
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
            LogUtil.d( TAG , "hide scrollSum=" + scrollSum + " , gap=" + gap )
            if( appbar_main?.visibility == View.VISIBLE && !isAppbarHiding ) {
                appbar_main?.startAnimation( animationAppbarHide )
            }
            if( toolbar_main?.visibility == View.VISIBLE && !isToolbarHiding ) {
                toolbar_main?.startAnimation( animationToolbarHide )
            }
        }
        // 위로 스크롤하면 보인다
        else if( scrollSum < -appbar_main.height) {
            LogUtil.d( TAG , "show scrollSum=" + scrollSum + " , gap=" + gap )
            if( appbar_main?.visibility != View.VISIBLE && !isAppbarShowing ) {
                appbar_main?.startAnimation( animationAppbarShow )
            }
            if( toolbar_main?.visibility != View.VISIBLE && !isToolbarShowing ) {
                toolbar_main?.startAnimation( animationToolbarShow )
            }
        }
    }

}
