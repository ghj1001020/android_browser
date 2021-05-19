package com.ghj.browser.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.ghj.browser.activity.adapter.data.WebSiteData
import org.json.JSONArray
import java.lang.Exception

class PreferenceHistoryUtil private constructor( val context : Context) {

    private val TAG : String = "PrefHistoryUtil"


    private lateinit var pref : SharedPreferences
    private val prefName = "preference_browser_history"

    private val pref_key = "WEB_PAGE_HISTORY_"

    init {
        pref = context.getSharedPreferences( prefName , Context.MODE_PRIVATE )
    }

    companion object {
        private var instance : PreferenceHistoryUtil? = null

        fun getInstance( context : Context ) : PreferenceHistoryUtil {
            if( instance == null ) {
                instance = PreferenceHistoryUtil( context )
            }
            return instance as PreferenceHistoryUtil
        }
    }

    fun saveWebPageHistory( items : ArrayList<String>? ) {
        if( items == null || items.size == 0 ) {
            return
        }

        setPrefArrayList( pref_key , items )
    }

    fun saveWebPageHistory( item : WebSiteData ) {
        val strItem = JsonUtil.dtoToJsonString( item )

        if( !TextUtils.isEmpty( strItem ) ) {
            val list : ArrayList<String> = getWebPageHistory()
            list.add( strItem )
            saveWebPageHistory( list )
        }
    }

    fun getWebPageHistory() : ArrayList<String> {
        return getPrefArrayList( pref_key )
    }


    private fun setPrefArrayList( key : String , items : ArrayList<String>? ) {
        if( TextUtils.isEmpty( key ) || items == null || items.isEmpty() ) {
            return
        }

        val jsonArray : JSONArray = JSONArray()

        for ( item in items ) {
            jsonArray.put( item )
        }

        val edit : SharedPreferences.Editor = pref.edit()
        edit.putString( key , jsonArray.toString() )
        edit.apply()
    }

    private fun getPrefArrayList( key : String ) : ArrayList<String> {
        val list = arrayListOf<String>()
        val str = pref.getString( key , "" )

        if( !TextUtils.isEmpty( str ) ) {
            try {
                val jsonArray = JSONArray( str )
                for( index in 0..jsonArray.length()-1 ) {
                    list.add( jsonArray.get( index ) as String )
                }
            }
            catch ( e : Exception ) {
                LogUtil.e("err=${e.message}" )
            }
        }

        return list
    }
}