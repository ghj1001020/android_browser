package com.ghj.browser.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.text.TextUtils
import android.webkit.CookieManager
import android.webkit.CookieSyncManager

object NetworkUtil {

    // network check
    fun checkNetworkConnection( context: Context) : Boolean {
        val cm : ConnectivityManager = context.getSystemService( Context.CONNECTIVITY_SERVICE ) as ConnectivityManager? ?: return false

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
            val capabilities : NetworkCapabilities = cm.getNetworkCapabilities( cm.activeNetwork ) ?: return false

            if( capabilities.hasTransport( NetworkCapabilities.TRANSPORT_WIFI ) || capabilities.hasTransport( NetworkCapabilities.TRANSPORT_CELLULAR ) ) {
                return true
            }
        }

        val networkInfo : NetworkInfo? = cm.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }

    // 특정 url의 key에 해당하는 쿠키값 구하기
    fun getCookieValue( url : String , key : String ) : String {
        val cookies : String = CookieManager.getInstance().getCookie( key )
        val arrCookies : List<String> = cookies.trim().split(";")

        var value = ""

        for( item : String in arrCookies ) {
            val arrItem : List<String> = item.split( "=" )
            if( arrItem.size < 2 ) continue

            if( key.equals( arrItem[0] , true ) ) {
                value = arrItem[1]
                break
            }
        }

        return value
    }

    // 특정 url의 쿠키 목록들
    fun getCookies( url : String ) : Map<String,String> {
        val cookieList = mutableMapOf<String,String>()    // 쿠키 목록 결과값

        val cookies = CookieManager.getInstance().getCookie( url )
        if(cookies != null) {
            val arrCookies : List<String> = cookies.trim().split( ";" )

            for( item : String in arrCookies ) {
                val arrItem : List<String> = item.split( "=" )
                if( arrItem.size < 2 ) continue

                cookieList.put( arrItem[0] , arrItem[1] )
            }
        }

        return cookieList
    }

    // 제이세션 쿠키 삭제
    fun removeSessionCookie() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            CookieManager.getInstance().removeSessionCookies(){
                if( it ) {
                    LogUtil.d("removeSessionCookie success" )
                }
                else {
                    LogUtil.d("removeSessionCookie fail" )
                }
            }
        }
        else {
            CookieManager.getInstance().removeSessionCookie()
        }
    }

    // 쿠키 삭제
    fun removeCookie() {
        val cookieManager : CookieManager = CookieManager.getInstance()
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            cookieManager.removeAllCookies(){
                if( it ) {
                    LogUtil.d("removeCookie success" )
                }
                else {
                    LogUtil.d("removeCookie fail" )
                }
            }
        }
        else {
            cookieManager.removeAllCookie()
        }

        flushCookies()
    }

    // 쿠키 추가
    fun addCookie( url : String , key : String , value : String ) {
        if( TextUtils.isEmpty( key ) ) return

        val cookieManager : CookieManager = CookieManager.getInstance()
        cookieManager.setCookie( url , "${key}=${value}" )

        flushCookies()
    }

    // 쿠키 여러개 추가
    fun addCookie( url : String , cookies : MutableMap<String,String> ) {
        val cookieManager : CookieManager = CookieManager.getInstance()

        for( key in cookies.keys ) {
            if( TextUtils.isEmpty( key ) ) continue
            cookieManager.setCookie( url , "${key}=${cookies[key]}" )
        }

        flushCookies()
    }

    // 쿠키 동기화(쿠키를 단말에 저장)
    fun flushCookies() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            CookieManager.getInstance().flush()
        }
        else {
            CookieSyncManager.getInstance().sync()
        }
    }
}