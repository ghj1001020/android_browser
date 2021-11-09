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

    // Url에서 파라미터 해시태그 제외한 Url만 반환
    fun removeParameterFromUrl( _url : String? ) : String {
        if( TextUtils.isEmpty( _url ) ) {
            return ""
        }

        var url = _url!!
        var index = _url.lastIndexOf("#")
        if( index > -1 ) {
            url = url.substring(0, index)
        }

        index = url.lastIndexOf("?")
        if( index > -1 ) {
            url = url.substring(0, index)
        }
        return url
    }

    // Url에서 확장자 반환
    fun getUrlExtension( _url : String? ) : String {
        if( TextUtils.isEmpty( _url ) ) {
            return ""
        }

        val url = _url!!.removeSuffix("/")
        var ext = ""
        val index = url.lastIndexOf(".")
        if( index > -1 && url.length > 1) {
            ext = url.substring(index+1)
        }
        return ext
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

    // null to string
    fun nullToString( text: String?, defaultValue : String? = "" ) : String
    {
        if( text == null )
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

        return text
    }

    // file name extension
    fun getFileExtension(text: String?) : String {
        if( TextUtils.isEmpty( text ) ) {
            return ""
        }

        return text?.split(".")?.last() as String
    }
}
