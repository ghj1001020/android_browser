package com.ghj.browser.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.*

object CommonUtil {

    // bitmap -> string 변환
    fun bitmapToString( bitmap : Bitmap? ) : String {
        if( bitmap == null ) {
            return ""
        }

        val os : ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress( Bitmap.CompressFormat.PNG , 100 , os )
        return Base64.encodeToString( os.toByteArray() , Base64.DEFAULT )
    }

    // string -> bitmap 변환
    fun stringToBitmap( str : String? ) : Bitmap? {
        if( TextUtils.isEmpty( str ) ) {
            return null
        }

        val bytes = Base64.decode( str , Base64.DEFAULT )
        return BitmapFactory.decodeByteArray( bytes , 0 , bytes.size )
    }

    // 날짜계산
    fun getDateDifference( date : Date , field : Int , amount : Int ) : Date {
        val calendar = Calendar.getInstance( Locale.getDefault() )
        calendar.time = date

        calendar.add( field , amount )  // Calendar.DATE : 날짜
        return calendar.time
    }
}