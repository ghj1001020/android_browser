package com.ghj.browser.util

import android.text.TextUtils
import java.io.File
import java.net.URL

object StringUtil {

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

    // Url에서 파일명 구하기
    fun getFilenameFromUrl( _url: String? ) : String {
        if( TextUtils.isEmpty( _url ) ) {
            return ""
        }

        val splitUrl = _url!!.split( "/")
        var filename : String = splitUrl[splitUrl.size - 1]

        return filename
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