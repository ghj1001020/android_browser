package com.ghj.browser.util

import android.text.TextUtils
import java.net.URL

class StringUtil {

    companion object {
        // Url에서 도메인만 반환
        fun getUrlDoamin( _url : String? ) : String {
            if( TextUtils.isEmpty( _url ) ) {
                return ""
            }

            try {
                val url = URL( _url )
                return url.host
            }
            catch ( e : Exception ) {
                return ""
            }
        }
    }
}

// null to string
fun String?.nullToString( defaultValue : String? = "" ) : String
{
    if( this == null )
    {
        if( !TextUtils.isEmpty( defaultValue ) )
        {

            return defaultValue as String
        }
        else
        {
            return ""
        }
    }

    return this as String
}

// file name extension
fun String?.getFileExtension() : String {
    if( TextUtils.isEmpty( this ) ) {
        return ""
    }

    var ext : String = this?.split(".")?.last() as String

    return ext
}