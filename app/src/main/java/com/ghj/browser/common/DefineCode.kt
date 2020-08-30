package com.ghj.browser.common

import android.app.Dialog

object DefineCode {

    // 기본페이지 - 네이버
    val DEFAULT_PAGE = "https://www.naver.com"

    // 인텐트 프로토콜
    val INTENT_PROTOCOL = "intent:"

    // 인텐트 파라미터명
    val IT_PARAM_COOKIE_URL = "it_param_cookie_url"

    // 핸들러 파라미터명
    val HDL_PARAM_WV_BRIDGE_RTN = "hdl_param_wv_bridge_rtn"

    // 핸들러 what
    val HDL_WHAT_JS_GET_MESSAGE = 0

    // URL 타입
    val URL_TYPE_HTTP = 0
    val URL_TYPE_TEL = 1
    val URL_TYPE_MAILTO = 2
    val URL_TYPE_MARKET = 3
    val URL_TYPE_SMS = 4
    val URL_TYPE_AUDIO = 5
    val URL_TYPE_VIDEO = 6
    val URL_TYPE_INTENT = 7

    // 다이얼로그 ID
    private val BASE_DIALOG_ID = 100
    val DIALOG_ID_JS_ALERT = BASE_DIALOG_ID + 1
    val DIALOG_ID_JS_CONFIRM = BASE_DIALOG_ID + 2

    // common dialog
    val BTN_OK = 0
    val BTN_LEFT = 1
    val BTN_RIGHT = 2

    // permission ID
    private val BASE_PERM_ID = 1000
    val PERM_ID_WEBVIEW_LOCATION = BASE_PERM_ID + 1
    val PERM_ID_WEBVIEW_WRITE_EXTERNAL_STORAGE = BASE_PERM_ID + 2

    // 툴바 > 더보기 메뉴 코드
    private val BASE_MORE_MENU = 2000
    val MORE_MENU_COOKIE = BASE_MORE_MENU + 1
    val MORE_MENU_PRINTER = BASE_MORE_MENU + 2

    // JS브릿지 타입
    private val BASE_JS_ID = 3000
    val JS_ALERT_POPUP = BASE_JS_ID + 1
}

public typealias CommonDialogCallback = (dialog : Dialog, dialogId : Int, selected : Int, data : String?)->Unit
public typealias ToolbarMoreDialogCallback = (dialog : Dialog , dialogId : Int , selected : Int )->Unit
public typealias CookieAddDialogCallback = (dialog : Dialog , selected : Int , data : String? )->Unit
