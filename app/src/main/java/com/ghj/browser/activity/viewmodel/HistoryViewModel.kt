package com.ghj.browser.activity.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.db.SQLiteService

class HistoryViewModel( application: Application ) : BaseViewModel(application) {

    val sqliteHistoryDatesAndUrls : MutableLiveData<MutableList<HistoryData>> = MutableLiveData()
    val sqliteHistoryUrlsByDate : MutableLiveData<MutableList<HistoryData>> = MutableLiveData()

    // 방문한사이트 날짜 그룹과 최근 날짜 URL목록
    fun selectHistoryDatesAndUrls(context: Context) {
        sqliteHistoryDatesAndUrls.value = SQLiteService.selectHistoryDatesAndUrls(context)
    }
}