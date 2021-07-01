package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ghj.browser.activity.adapter.data.ConsoleData
import com.ghj.browser.db.SQLiteService

class ConsoleViewModel( application: Application ) : BaseViewModel(application) {

    // 콘솔로그 목록 데이터
    val consoleLogDataUpdate : MutableLiveData<ArrayList<ConsoleData>> = MutableLiveData()
    // 콘솔로그 모든데이터 삭제결과
    val consoleLogDataDeleteAll : MutableLiveData<Boolean> = MutableLiveData()

    // 옵저버 등록
    fun addObserver(owner: LifecycleOwner, observer1: Observer<ArrayList<ConsoleData>>, observer2: Observer<Boolean>) {
        consoleLogDataUpdate.observe(owner, observer1)
        consoleLogDataDeleteAll.observe(owner, observer2)
    }

    // 옵저버 해제
    fun removeObserver(observer1: Observer<ArrayList<ConsoleData>>, observer2: Observer<Boolean>) {
        consoleLogDataUpdate.removeObserver(observer1)
        consoleLogDataDeleteAll.removeObserver(observer2)
    }

    // 콘솔로그 목록 조회
    fun queryConsoleLogData(context: Context) {
        val list : ArrayList<ConsoleData> = SQLiteService.selectConsoleLogData(context)
        consoleLogDataUpdate.value = list
    }

    // 콘솔로그 모든 데이터 삭제
    fun queryConsoleLogDataDeleteAll(context: Context) {
        consoleLogDataDeleteAll.value = SQLiteService.deleteConsoleLogDataAll(context)
    }
}