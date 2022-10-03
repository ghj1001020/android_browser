package com.ghj.browser.util

import android.text.TextUtils
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.Exception
import java.lang.reflect.Type

object JsonUtil {

    // array -> json string
    fun <T> arrayToJsonString( list: ArrayList<T>? ) : String {
        if(list == null) {
            return ""
        }

        try {
            val builder : GsonBuilder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            builder.serializeNulls()

            val gson : Gson = builder.create()

            return gson.toJson( list )
        }
        catch ( e : Exception ) {
            LogUtil.e("err = " + e.message )
            return ""
        }
    }

    // json string -> array
    fun <T> parseArray(json: String, type: Type) : ArrayList<T>? {
        if( TextUtils.isEmpty(json) || "{}" == json ) {
            return null
        }

        try {
            val builder : GsonBuilder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            builder.serializeNulls()

            val gson : Gson = builder.create()

            return gson.fromJson(json, type)
        }
        catch ( e : Exception) {
            LogUtil.e("err = " + e.message )
            return null
        }
    }

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
            LogUtil.e("err = " + e.message )
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
            LogUtil.e("err = " + e.message )
            return null
        }
    }

    // json string -> map
    fun parseMap( json : String? ) : Map<String, String>? {
        if( TextUtils.isEmpty(json) || "{}" == json ) {
            return null
        }

        try {
            val builder : GsonBuilder = GsonBuilder()
            builder.excludeFieldsWithoutExposeAnnotation()
            builder.serializeNulls()

            val gson : Gson = builder.create()

            return gson.fromJson(json, object : TypeToken<Map<String, String>>(){}.type)
        }
        catch ( e : Exception) {
            e.printStackTrace()
            LogUtil.e("err = " + e.message )
            return null
        }
    }
}