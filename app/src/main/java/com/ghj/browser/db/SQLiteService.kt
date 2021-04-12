package com.ghj.browser.db

import android.content.Context
import android.database.Cursor
import com.ghj.browser.activity.adapter.HistoryAdapter
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.common.DefineQuery
import com.ghj.browser.common.HistoryType

object SQLiteService {

    // HISTORY_TBL에 데이터 입력
    fun insertHistoryData( context: Context, params: Array<String> ) {
        SQLite.init(context)
        SQLite.execSQL(DefineQuery.INSERT_HISTORY_URL, params)
        SQLite.close()
    }

    // HISTORY_TBL의 favicon 수정
    fun updateHistoryFavIcon( context: Context, params: Array<String> ) {
        SQLite.init(context)
        SQLite.execSQL(DefineQuery.UPDATE_HISTORY_FAVICON, params)
        SQLite.close()
    }

    // HISTORY_TBL의 날짜그룹, 오늘날짜 히스토리 목록 조회
    fun selectHistoryDatesAndUrls( context: Context ) : ArrayList<HistoryData> {
        val historyList : ArrayList<HistoryData> = arrayListOf()
        val params : Array<String> = Array(1) { i -> ""}

        SQLite.init(context)

        // 날짜그룹
        SQLite.select( DefineQuery.SELECT_HISTORY_DATE_GROUP ) { cursor: Cursor ->
            while( cursor.moveToNext() ) {
                val date = cursor.getString( cursor.getColumnIndex("DATE") )
                historyList.add( HistoryData(HistoryType.DATE, date))
            }
        }

        // 최근날짜 히스토리 목록
        if( historyList.size > 0 ) {
            params[0] = historyList.get(0).date
            historyList.get(0).isOpen = true
            SQLite.select( DefineQuery.SELECT_HISTORY_URL_BY_DATE, params ) { cursor: Cursor ->
                val list : MutableList<HistoryData> = mutableListOf()
                while( cursor.moveToNext() ) {
                    val date : String = cursor.getString( cursor.getColumnIndex("VISIT_DATE") )
                    val title : String = cursor.getString( cursor.getColumnIndex("TITLE") )
                    val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                    val favIcon : String = cursor.getString( cursor.getColumnIndex("FAVICON") )
                    list.add( HistoryData(HistoryType.URL, date, title, url, favIcon) )
                }

                historyList.addAll(1, list)
            }
        }

        SQLite.close()

        return historyList;
    }

    // 해당날짜에 방문한 HISTORY_TBL의 URL개수
    fun selectHistoryCntByDate(context: Context, date: String) : Int {
        var cnt = 0

        SQLite.init(context)

        val params : Array<String> = arrayOf(date)
        SQLite.select( DefineQuery.SELECT_HISTORY_CNT_BY_DATE, params) { cursor: Cursor ->
            cursor.moveToNext()
            cnt = cursor.getInt( cursor.getColumnIndex("CNT") )
        }

        SQLite.close()

        return cnt
    }

    // 해당날짜에 방문한 HISTORY_TBL의 URL목록
    fun selectHistoryUrlsByDate(context: Context, date: String) : ArrayList<HistoryData> {
        val historyList : ArrayList<HistoryData> = arrayListOf()

        SQLite.init(context)

        val param = arrayOf(date)
        SQLite.select( DefineQuery.SELECT_HISTORY_URL_BY_DATE, param) { cursor: Cursor ->
            while( cursor.moveToNext() ) {
                val date : String = cursor.getString( cursor.getColumnIndex("VISIT_DATE") )
                val title : String = cursor.getString( cursor.getColumnIndex("TITLE") )
                val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                val favIcon : String = cursor.getString( cursor.getColumnIndex("FAVICON") )

                historyList.add( HistoryData(HistoryType.URL, date, title, url, favIcon) )
            }
        }

        SQLite.close()

        return historyList
    }

    // HISTORY_TBL의 모든 데이터 삭제
    fun deleteHistoryDataAll(context: Context) {
        SQLite.init(context)
        SQLite.execSQL(DefineQuery.DELETE_HISTORY_DATA_ALL)
        SQLite.close()
    }
}