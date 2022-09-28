package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.util.JsonUtil
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class WebViewLogViewModel( application: Application ) : BaseViewModel(application) {

    // 웹뷰로그 그룹 데이터
    var webViewLogGroupList : MutableLiveData<ArrayList<WebViewLogData>> = MutableLiveData()
    // 웹뷰로그 목록 데이터
    var webViewLogDataList : MutableLiveData<JSONObject> = MutableLiveData()
    // 웹뷰로그 모든데이터 삭제결과
    var webViewLogDataDeleteAll : MutableLiveData<Boolean> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<WebViewLogData>>, observer2: Observer<JSONObject>, observer3: Observer<Boolean>) {
        webViewLogGroupList.observe(owner, observer1)
        webViewLogDataList.observe(owner, observer2)
        webViewLogDataDeleteAll.observe(owner, observer3)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<WebViewLogData>>, observer2: Observer<JSONObject>, observer3: Observer<Boolean>) {
        webViewLogGroupList.removeObserver(observer1)
        webViewLogDataList.removeObserver(observer2)
        webViewLogDataDeleteAll.removeObserver(observer3)
    }


    // 웹뷰로그 그룹 목록조회
    fun queryWebViewLogData(context: Context) {
        val list : ArrayList<WebViewLogData> = SQLiteService.selectWebViewLogData( context )
        webViewLogGroupList.value = list
    }

    // 웹뷰로그 URL별 목록조회
    fun queryWebViewLogDataByUrl(context: Context, position: Int, data: WebViewLogData) {
        val list : ArrayList<WebViewLogData> = SQLiteService.selectWebViewLogsByUrl(context, data.insertTime, data.url)
        val jsonString = JsonUtil.arrayToJsonString(list)

        val json = JSONObject()
        json.put("position", position)
        json.put("list", jsonString)
        webViewLogDataList.value = json
    }

    // 웹뷰로그 모든데이터 삭제
    fun queryWebViewLogDataDeleteAll(context: Context) {
        webViewLogDataDeleteAll.value = SQLiteService.deleteWebViewLogDataAll(context)
    }
}