package com.ghj.browser.webkit

import android.webkit.SslErrorHandler
import android.webkit.WebView

interface OnWebViewListener {

    // WebViewClient
    fun onPageStarted( _webView: WebView , urlType: Int , url: String )
    fun shouldOverrideLoading( _webView: WebView , urlType: Int , url: String , isRedirect: Boolean )
    fun onPageFinished( _webView: WebView, url: String )
    fun onReceivedError( _webView: WebView, errorCode: Int, url: String, failUrl: String )
    fun onReceivedHttpError( _webView: WebView, errorCode: Int, url: String, failUrl: String)
    fun onReceivedSslError( _webView: WebView, handler: SslErrorHandler, errorCode: Int, url: String, failUrl: String)

    // WebChromeClient
    fun onReceivedTitle( _webView: WebView, title: String )
    fun onProgressChanged( _webView: WebView, newProgress: Int )
    fun onRequestPermissions( requestCode : Int , permissionResult : Int , permissions : Array<String> )

    // WebView
    fun onScrollChanged( t: Int , oldt: Int )
}