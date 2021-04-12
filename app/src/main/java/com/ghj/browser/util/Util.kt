package com.ghj.browser.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Util {

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

    // date string을 from format -> to format 으로 변환
    fun convertDateFormat( date: String, fromFormat: String, toFormat: String ) : String {
        val from = SimpleDateFormat(fromFormat, Locale.getDefault())
        val to = SimpleDateFormat(toFormat, Locale.getDefault())

        try {
            return to.format(from.parse(date)!!)
        }
        catch ( e: Exception ) {
            return ""
        }
    }

    // date string에서 요일 구하기 (1-일요일 ~ 7-토요일, 0-에러)
    fun getDayOfWeekFromDateString( date: String, format: String ) : Int {
        val sdf = SimpleDateFormat(format, Locale.getDefault() )

        var dayOfWeek = 0
        try {
            sdf.parse(date)?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            }
        }
        catch ( e: Exception ) { }

        return dayOfWeek
    }
}