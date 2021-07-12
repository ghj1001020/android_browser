package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.db.SQLiteService

class BookmarkViewModel( application: Application ) : BaseViewModel(application) {

    // 즐겨찾기 목록 데이터
    var bookmarkDataList : MutableLiveData<ArrayList<BookmarkData>> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<BookmarkData>>) {
        bookmarkDataList.observe(owner, observer1)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<BookmarkData>>) {
        bookmarkDataList.removeObserver(observer1)
    }


    // 즐겨찾기 목록 조회
    fun queryBookmarkData(context: Context) {
        val list : ArrayList<BookmarkData> = SQLiteService.selectBookmarkData(context)
        bookmarkDataList.value = list
    }
}