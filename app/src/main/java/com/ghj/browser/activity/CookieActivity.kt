package com.ghj.browser.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.CookieAdapter
import com.ghj.browser.activity.adapter.data.CookieData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import com.ghj.browser.dialog.CookieAddDialog
import com.ghj.browser.util.CookieUtil
import com.ghj.browser.util.LogUtil
import kotlinx.android.synthetic.main.activity_cookie.*
import kotlinx.android.synthetic.main.appbar_cookie.*

class CookieActivity : BaseActivity() , View.OnClickListener {

    private val TAG = "CookieActivity"


    // ui
    var actionBar : ActionBar? = null
    lateinit var cookieAdapter : CookieAdapter

    // dialog
    var dialog : Dialog? = null

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
        LogUtil.d("domain=" + domain )
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

        // 쿠키목록
        val cookieData : ArrayList<CookieData> = arrayListOf()
        val cookies : Map<String,String> = CookieUtil.getCookies( domain )
        for( cookie in cookies ) {
            cookieData.add( CookieData( cookie.key , cookie.value ) )
        }

        cookieAdapter = CookieAdapter( this , cookieData )
        list_cookies.adapter = cookieAdapter

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            list_cookies.isNestedScrollingEnabled = true
            list_cookies.startNestedScroll( View.OVER_SCROLL_ALWAYS )
        }

        txt_toolbar_sub_desc1.text = String.format( getString(R.string.toolbar_more_cookie_desc) , cookieAdapter.count )
        txt_toolbar_sub_desc2.text = domain

        btn_back.setOnClickListener( this )
        btn_cookie_add.setOnClickListener( this )
    }

    override fun onClick(p0: View?) {
        when( p0?.id ) {
            R.id.btn_back -> {

            }

            R.id.btn_cookie_add -> {
                onCookieAdd()
            }
        }
    }

    // 쿠키추가 다이얼로그
    fun onCookieAdd() {
        dialog?.dismiss()

        dialog = CookieAddDialog( this ) { dialog: Dialog, selected: Int, data: String? ->

        }
        dialog?.show()
    }
}