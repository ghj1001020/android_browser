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
    // 즐겨찾기 삭제결과 데이터
    var bookmarkDataDelete : MutableLiveData<Boolean> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<BookmarkData>>, observer2: Observer<Boolean>) {
        bookmarkDataList.observe(owner, observer1)
        bookmarkDataDelete.observe(owner, observer2)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<BookmarkData>>, observer2: Observer<Boolean>) {
        bookmarkDataList.removeObserver(observer1)
        bookmarkDataDelete.removeObserver(observer2)
    }


    // 즐겨찾기 목록 조회
    fun queryBookmarkData(context: Context) {
        val list : ArrayList<BookmarkData> = SQLiteService.selectBookmarkData(context)
        bookmarkDataList.value = list
    }

    // 즐겨찾기 선택 후 삭제
    fun queryBookmarkDataDelete(context: Context, params: ArrayList<String>) {
        bookmarkDataDelete.value = SQLiteService.deleteBookmarkData(context, params)
    }

    // 즐겨찾기 전체 삭제
    fun queryBookmarkDataDeleteAll(context: Context) {
        bookmarkDataDelete.value = SQLiteService.deleteBookmarkDataAll(context)
    }
}