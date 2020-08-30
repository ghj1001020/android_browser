package com.ghj.browser.webkit

import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.LogUtil

class JsBridge( var callback : JsCallback ) {

    private val TAG = "JsBridge"


    public interface JsCallback {
        fun onRequestFromJs( requestId : Int , vararg params : String? )
    }

    // Js -> Native 호출
    @JavascriptInterface
    fun appAlertPopup( json : String? ) {
        LogUtil.d( TAG , "appAlertPopup : ${json}" )
        callback.onRequestFromJs( DefineCode.JS_ALERT_POPUP , json )
    }

}