package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.db.SQLiteService

class MainViewModel( application: Application) : BaseViewModel(application) {

    // 즐겨찾기 목록 데이터
    var bookmarkDataList : MutableLiveData<ArrayList<BookmarkData>> = MutableLiveData()
    // 히스토리 목록 데이터
    var historyDataList : MutableLiveData<ArrayList<WebSiteData>> = MutableLiveData()
    // 히스토리 검색 데이터
    var historySearchDataList : MutableLiveData<ArrayList<WebSiteData>> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<BookmarkData>>, observer2: Observer<ArrayList<WebSiteData>>) {
        bookmarkDataList.observe(owner, observer1)
        historyDataList.observe(owner, observer2)
        historySearchDataList.observe(owner, observer2)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<BookmarkData>>, observer2: Observer<ArrayList<WebSiteData>>) {
        bookmarkDataList.removeObserver(observer1)
        historyDataList.removeObserver(observer2)
        historySearchDataList.removeObserver(observer2)
    }


    // 즐겨찾기 목록조회
    fun queryBookmarkData(context: Context) {
        bookmarkDataList.value = SQLiteService.selectBookmarkData(context)
    }

    // 히스토리 목록조회
    fun queryHistoryData(context: Context) {
        historyDataList.value = SQLiteService.selectHistoryUrl(context)
    }

    // 히스토리 검색
    fun searchHistoryData(search: String) {
        val searchList: ArrayList<WebSiteData> = arrayListOf()
        val _search = search.toLowerCase()

        if(historyDataList.value != null) {
            for(item in historyDataList.value!!) {
                if(item.title.toLowerCase().contains(_search) || item.url.toLowerCase().contains(_search)) {
                    searchList.add(item)
                }
            }
        }

        historySearchDataList.value?.clear()
        historySearchDataList.value?.addAll(searchList)
    }
}
