package com.ghj.browser.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.CookieAdapter
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import kotlinx.android.synthetic.main.activity_cookie.*
import kotlinx.android.synthetic.main.appbar_cookie.*

class CookieActivity : BaseActivity() {

    // ui
    var actionBar : ActionBar? = null
    lateinit var cookieAdapter : CookieAdapter

    var domain : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView( R.layout.activity_cookie)

        initData( intent )
        initLayout()
    }

    override fun onCreateAfter() {

    }

    fun initData( intent : Intent ) {
        domain = intent.getStringExtra( DefineCode.IT_PARAM_COOKIE_URL ) ?: ""
    }

    fun initLayout() {
        setSupportActionBar(appbar_cookie)
        actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled( true )  // 커스터마이징 하기 위해 필요
            it.setDisplayHomeAsUpEnabled( false )  // 뒤로가기 버튼 표시여부, 디폴트로 true만 해도 백버튼이 생김
            it.setDisplayShowTitleEnabled( false )   // 액션바에 표시되는 제목의 표시여부
            it.setDisplayShowHomeEnabled( false )   // 홈 아이콘 표시여부
        }

        // todo 테스트 하드코딩
        val cookieData = arrayListOf<String>()
        cookieData.add( "1111" )
        cookieData.add( "2222" )
        cookieData.add( "3333" )
        cookieData.add( "4444" )
        cookieData.add( "5555" )
        cookieData.add( "6666" )
        cookieData.add( "7777" )
        cookieData.add( "8888" )
        cookieData.add( "9999" )
        cookieData.add( "0000" )
        cookieData.add( "ㅁㅁㅁ" )
        cookieData.add( "ㄴㄴㄴㄴ" )
        cookieData.add( "ㅇㅇㅇㅇ" )
        cookieData.add( "DDDD" )
        cookieData.add( "EEEE" )
        cookieData.add( "ffff" )
        cookieData.add( "dddd" )
        cookieData.add( "1" )
        cookieData.add( "2" )
        cookieData.add( "3" )
        cookieData.add( "4" )
        cookieData.add( "5" )
        cookieData.add( "6" )
        cookieData.add( "7" )


        cookieAdapter = CookieAdapter( this , cookieData )
        list_cookies.adapter = cookieAdapter
        list_cookies.isNestedScrollingEnabled = true

    }
}