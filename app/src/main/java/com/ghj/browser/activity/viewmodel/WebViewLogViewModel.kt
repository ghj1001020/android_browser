package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.db.SQLiteService

class WebViewLogViewModel( application: Application ) : BaseViewModel(application) {

    // 웹뷰로그 목록 데이터
    var webViewLogDataList : MutableLiveData<ArrayList<WebViewLogData>> = MutableLiveData()
    // 웹뷰로그 모든데이터 삭제결과
    var webViewLogDataDeleteAll : MutableLiveData<Boolean> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<WebViewLogData>>, observer2: Observer<Boolean>) {
        webViewLogDataList.observe(owner, observer1)
        webViewLogDataDeleteAll.observe(owner, observer2)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<WebViewLogData>>, observer2: Observer<Boolean>) {
        webViewLogDataList.removeObserver(observer1)
        webViewLogDataDeleteAll.removeObserver(observer2)
    }


    // 웹뷰로그 목록조회
    fun queryWebViewLogData(context: Context) {
        val list : ArrayList<WebViewLogData> = SQLiteService.selectWebViewLogData( context )
        webViewLogDataList.value = list
    }

    // 웹뷰로그 모든데이터 삭제
    fun queryWebViewLogDataDeleteAll(context: Context) {
        webViewLogDataDeleteAll.value = SQLiteService.deleteWebViewLogDataAll(context)
    }
}