package com.ghj.browser.common

import android.app.Dialog

// 목록 클릭 리스너
interface IClickListener {
    fun onItemClick( position: Int, url: String )
}

// 웹사이트 목록 구분 타입
enum class WebSiteType(val value: Int) {
    DATE(0) ,   // 날짜
    URL(1)  ,   // 방문사이트
    Empty(2)
}

// 히스토리 목록 작업 타입
enum class JobMode(val value: Int) {
    VIEW(0) ,   // 조회
    DELETE(1) , // 삭제
    SHARE(2)    // 공유
}

public typealias CommonDialogCallback = (dialog : Dialog, dialogId : Int, selected : Int, data : String?)->Unit
public typealias CookieAddDialogCallback = (dialog : Dialog)->Unit
public typealias ScriptInputDialogCallback = (dialog: Dialog, script: String) -> Unit
