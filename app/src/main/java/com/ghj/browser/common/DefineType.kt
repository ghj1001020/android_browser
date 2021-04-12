package com.ghj.browser.common

import android.app.Dialog

// 목록 클릭 리스너
interface IClickListener {
    fun onItemClick( position: Int )
}

// 히스토리 목록 구분 타입
enum class HistoryType(val value: Int) {
    DATE(0) ,   // 날짜
    URL(1)      // 방문사이트
}

public typealias CommonDialogCallback = (dialog : Dialog, dialogId : Int, selected : Int, data : String?)->Unit
public typealias ToolbarMoreDialogCallback = (dialog : Dialog, dialogId : Int, selected : Int )->Unit
public typealias CookieAddDialogCallback = (dialog : Dialog, selected : Int, data : String? )->Unit
