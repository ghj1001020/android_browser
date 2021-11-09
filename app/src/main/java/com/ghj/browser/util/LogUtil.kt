package com.ghj.browser.util

import android.text.TextUtils
import android.util.Log
import com.ghj.browser.BuildConfig


object LogUtil {

    private val TAG : String = "Browser"

    private val MAX_MSG = 1500

    private val LEVEL_DEBUG = 0
    private val LEVEL_ERROR = 1

    fun d( _message: String? ) {
        if( BuildConfig.BUILD_CONFIG_IS_LOG_BLOCK ) {
            return
        }

        val message = StringUtil.nullToString(_message)
        printLog(LEVEL_DEBUG, message)
    }

    fun e( _message: String? ) {
        if( BuildConfig.BUILD_CONFIG_IS_LOG_BLOCK ) {
            return
        }

        val message = StringUtil.nullToString(_message)
        printLog(LEVEL_ERROR, message)
    }

    private fun printLog(LEVEL : Int , message: String) {
        // 헤더
        var className = Thread.currentThread().stackTrace[4].className
        if( !TextUtils.isEmpty(className) ) {
            className = className.substring( className.lastIndexOf(".")+1)
        }
        val methodName = Thread.currentThread().stackTrace[4].methodName
        val lineNumber = Thread.currentThread().stackTrace[4].lineNumber
        val head = "[${className} > ${methodName}() Line:${lineNumber}] >> "

        // 글자수 제한
        val length : Int = message.length/MAX_MSG +1
        for( idx in 0..length-1 ) {
            // 바디
            val lastIdx = if ((idx+1)*MAX_MSG > message.length) {
                message.length
            }
            else {
                (idx+1)*MAX_MSG
            }
            val body = message.substring(idx* MAX_MSG , lastIdx)

            // 출력
            when(LEVEL) {
                LEVEL_DEBUG ->
                    Log.d(TAG, "${head}${body}")

                LEVEL_ERROR ->
                    Log.e(TAG, "${head}${body}")
            }
        }
    }
}