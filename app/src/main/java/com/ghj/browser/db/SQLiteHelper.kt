package com.ghj.browser.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ghj.browser.common.DefineQuery

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, SQLite.DB_FILE_NAME, null, SQLite.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        // 히스토리 테이블 생성
        db?.execSQL(DefineQuery.CREATE_HISTORY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DefineQuery.DROP_HISTORY_TABLE)
        onCreate(db)
    }

}