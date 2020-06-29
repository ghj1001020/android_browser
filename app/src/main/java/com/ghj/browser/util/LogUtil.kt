package com.ghj.browser.util

import android.util.Log
import com.ghj.browser.BuildConfig


class LogUtil {

    companion object {

        fun d( _tag: String? , _message: String? ) {
            if( BuildConfig.BUILD_CONFIG_IS_LOG_BLOCK ) {
                return
            }

            val tag = _tag.nullToString()
            val message = _message.nullToString()
            Log.d( tag, message )
        }

        fun e( _tag: String? , _message: String? ) {
            if( BuildConfig.BUILD_CONFIG_IS_LOG_BLOCK ) {
                return
            }

            val tag = _tag.nullToString()
            val message = _message.nullToString()
            Log.e( tag, message )
        }
    }
}