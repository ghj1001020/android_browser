package com.ghj.browser.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils



class PreferenceUtil private constructor( val context : Context ) {

    private lateinit var pref : SharedPreferences
    private val prefName = "preference_browser"

    init {
        pref = context.getSharedPreferences( prefName , Context.MODE_PRIVATE )
    }

    companion object {
        private var instance : PreferenceUtil? = null

        fun getInstance( context : Context ) : PreferenceUtil {
            if( instance == null ) {
                instance = PreferenceUtil( context )
            }
            return instance as PreferenceUtil
        }
    }


    fun setPreference( key : String , value : String ) {
        if( TextUtils.isEmpty( key ) ) {
            return
        }

        val edit : SharedPreferences.Editor = pref.edit()
        edit.putString( key , value )
        edit.commit()
    }

    fun setPreference( key : String , value : Boolean ) {
        if( TextUtils.isEmpty( key ) ) {
            return
        }

        val edit : SharedPreferences.Editor = pref.edit()
        edit.putBoolean( key , value )
        edit.commit()
    }

    fun getPreference( key : String , defaultValue : String = "" ) : String {
        return pref.getString( key , defaultValue ) ?: ""
    }

    fun getPreferenceBoolean( key : String , defaultValue : Boolean = false ) : Boolean {
        return pref.getBoolean( key , defaultValue )
    }


    // KEY
    private val PREFIX = "BROWSER_KEY_"
}
