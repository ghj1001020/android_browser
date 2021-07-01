package com.ghj.browser.db

import android.content.Context
import android.database.Cursor
import android.text.TextUtils
import com.ghj.browser.activity.adapter.data.ConsoleData
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.common.DefineQuery
import com.ghj.browser.common.WebSiteType

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
    fun selectHistoryDatesAndUrls( context: Context ) : ArrayList<WebSiteData> {
        val webSiteList : ArrayList<WebSiteData> = arrayListOf()
        val params : Array<String> = Array(1) { i -> ""}

        SQLite.init(context)

        // 날짜그룹
        SQLite.select( DefineQuery.SELECT_HISTORY_DATE_GROUP ) { cursor: Cursor ->
            while( cursor.moveToNext() ) {
                val date = cursor.getString( cursor.getColumnIndex("DATE") )
                webSiteList.add( WebSiteData(WebSiteType.DATE, date))
            }
        }

        // 최근날짜 히스토리 목록
        if( webSiteList.size > 0 ) {
            params[0] = webSiteList.get(0).date
            webSiteList.get(0).isOpen = true
            SQLite.select( DefineQuery.SELECT_HISTORY_URL_BY_DATE, params ) { cursor: Cursor ->
                val list : MutableList<WebSiteData> = mutableListOf()
                while( cursor.moveToNext() ) {
                    val date : String = cursor.getString( cursor.getColumnIndex("VISIT_DATE") )
                    val title : String = cursor.getString( cursor.getColumnIndex("TITLE") )
                    val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                    val favIcon : String = cursor.getString( cursor.getColumnIndex("FAVICON") )
                    list.add( WebSiteData(WebSiteType.URL, date, title, url, favIcon) )
                }

                webSiteList.addAll(1, list)
            }
        }

        SQLite.close()

        return webSiteList;
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
    fun selectHistoryUrlsByDate(context: Context, date: String) : ArrayList<WebSiteData> {
        val webSiteList : ArrayList<WebSiteData> = arrayListOf()

        SQLite.init(context)

        val param = arrayOf(date)
        SQLite.select( DefineQuery.SELECT_HISTORY_URL_BY_DATE, param) { cursor: Cursor ->
            while( cursor.moveToNext() ) {
                val date : String = cursor.getString( cursor.getColumnIndex("VISIT_DATE") )
                val title : String = cursor.getString( cursor.getColumnIndex("TITLE") )
                val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                val favIcon : String = cursor.getString( cursor.getColumnIndex("FAVICON") )

                webSiteList.add( WebSiteData(WebSiteType.URL, date, title, url, favIcon) )
            }
        }

        SQLite.close()

        return webSiteList
    }

    // HISTORY_TBL의 모든 데이터 삭제
    fun deleteHistoryDataAll(context: Context) {
        SQLite.init(context)
        SQLite.execSQL(DefineQuery.DELETE_HISTORY_DATA_ALL)
        SQLite.close()
    }

    // HISTORY_TBL의 데이터 삭제
    fun deleteHistoryData(context: Context, params: ArrayList<String>) {
        val whereParam : String = params.joinToString(" OR ") { s: String ->
            "VISIT_DATE='$s'"
        }

        if( !TextUtils.isEmpty(whereParam) ) {
            SQLite.init(context)
            SQLite.execSQL(DefineQuery.DELETE_HISTORY_DATA + whereParam)
            SQLite.close()
        }
    }

    // HISTORY_TBL의 검색 목록
    fun selectHistorySearch(context: Context, search: String) : ArrayList<WebSiteData> {
        val webSiteList: ArrayList<WebSiteData> = arrayListOf()
        SQLite.init(context)

        val param = arrayOf(search, search)
        SQLite.select(DefineQuery.SELECT_HISTORY_SEARCH, param) {cursor: Cursor ->
            while(cursor.moveToNext()) {
                val date : String = cursor.getString( cursor.getColumnIndex("VISIT_DATE") )
                val title : String = cursor.getString( cursor.getColumnIndex("TITLE") )
                val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                val favIcon : String = cursor.getString( cursor.getColumnIndex("FAVICON") )

                webSiteList.add( WebSiteData(WebSiteType.URL, date, title, url, favIcon) )
            }
        }
        SQLite.close()

        return webSiteList
    }

    // CONSOLE_LOG_TBL에 데이터 입력
    fun insertConsoleLogData(context: Context, params: Array<String>) {
        SQLite.init(context)
        SQLite.execSQL(DefineQuery.INSERT_CONSOLE_LOG, params)
        SQLite.close()
    }

    // CONSOLE_LOG_TBL의 목록 조회
    fun selectConsoleLogData(context: Context) : ArrayList<ConsoleData> {
        val list : ArrayList<ConsoleData> = arrayListOf()

        SQLite.init(context)
        SQLite.select(DefineQuery.SELECT_CONSOLE_LOG) {cursor: Cursor ->
            while(cursor.moveToNext()) {
                val date : String = cursor.getString( cursor.getColumnIndex("LOG_DATE") )
                val url : String = cursor.getString( cursor.getColumnIndex("URL") )
                val log : String = cursor.getString( cursor.getColumnIndex("LOG") )
                list.add( ConsoleData(date, url, log))
            }
        }
        SQLite.close()

        return list
    }

    fun deleteConsoleLogDataAll(context: Context) : Boolean {
        SQLite.init(context)
        val result = SQLite.execSQL(DefineQuery.DELETE_CONSOLE_LOG_DATA_ALL)
        SQLite.close()

        return result
    }
}