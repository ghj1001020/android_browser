package com.ghj.browser.util

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.Exception

object JsonUtil {

    private val TAG : String = "JsonUtil"

    // dto -> json string
    fun <T> dtoToJsonString( dto : T? ) : String {
        if( dto == null ) {
            return ""
        }

        try {
            val builder : GsonBuilder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            builder.serializeNulls()

            val gson : Gson = builder.create()

            return gson.toJson( dto )
        }
        catch ( e : Exception) {
            LogUtil.e( TAG , "err = " + e.message )
            return ""
        }
    }

    // json string -> dto
    fun <T> parseJson( json : String? , dto : Class<T> ) : T? {
        if( TextUtils.isEmpty(json) || "{}" == json ) {
            return null
        }

        try {
            val builder : GsonBuilder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            builder.serializeNulls()

            val gson : Gson = builder.create()

            return gson.fromJson( json , dto )
        }
        catch ( e : Exception) {
            LogUtil.e( TAG , "err = " + e.message )
            return null
        }
    }
}