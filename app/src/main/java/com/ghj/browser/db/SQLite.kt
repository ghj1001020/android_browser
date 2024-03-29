package com.ghj.browser.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.lang.Exception

object SQLite {
    val DB_FILE_NAME = "browser.db"
    val DB_VERSION = 12

    private var helper : SQLiteHelper? = null
    private var readDB : SQLiteDatabase? = null
    private var writeDB : SQLiteDatabase? = null


    fun init( context: Context ) {
        if( helper == null ) {
            helper = SQLiteHelper(context)
            readDB = helper?.readableDatabase
            writeDB = helper?.writableDatabase
        }
    }

    fun close() {
        readDB?.close()
        writeDB?.close()
        helper?.close()
        helper = null
    }

    // SELECT
    fun select( sql: String, params: Array<String>?=null, listener: (cursor: Cursor)->Unit ) {
        synchronized(this) {
            try {
                if( readDB == null ) return

                val cursor: Cursor = readDB!!.rawQuery( sql, params )
                listener(cursor)
                cursor.close()
            }
            catch( e: Exception ) {
                e.printStackTrace()
            }
        }
    }

    // INSERT, UPDATE, DELETE
    fun execSQL( sql: String, params: Array<String>?=null ) : Boolean {
        synchronized(this) {
            writeDB?.beginTransaction()

            try {
                if( writeDB == null ) return false

                if( params == null ) {
                    writeDB!!.execSQL(sql)
                }
                else {
                    writeDB!!.execSQL(sql, params)
                }
                return true
            }
            catch( e: Exception ) {
                e.printStackTrace()
                return false
            }
            finally {
                writeDB?.setTransactionSuccessful()
                writeDB?.endTransaction()
            }
        }
    }
}