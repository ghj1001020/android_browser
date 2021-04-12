package com.ghj.browser.activity.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.ghj.browser.BrowserApp
import com.ghj.browser.util.LogUtil
import java.lang.Exception

abstract class BaseActivity : AppCompatActivity() {

    private var TAG : String = "BaseActivity"

    // 앱 실행 가능 여부
    private var isAppReady : Boolean = false
    private var isPostCreate : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkActivity()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        isPostCreate = true;

        if( isAppReady && isPostCreate ) {
            onCreateAfter()
        }
    }

    // 액티비티 실행시 기본적인 체크
    private fun checkActivity()
    {
        isAppReady = true

        // 로직 수행
        if( isPostCreate && isAppReady )
        {
            onCreateAfter()
        }
    }

    abstract fun onCreateAfter()   // 앱 실행 후 기본적인 체크 후 액티비티 로직 실행\


    // 설정 > 권한화면으로 이동
    fun moveToPermSetting( requestCode : Int )
    {
        try {
            val intent = Intent( Settings.ACTION_APPLICATION_DETAILS_SETTINGS )
            intent.data = Uri.parse( "package:" + packageName )
            startActivityForResult( intent, requestCode )
        }
        catch ( e1 : Exception) {
            try {
                val intent = Intent( Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS ) // 설정 > 어플리케이션 목록
                startActivityForResult( intent, requestCode )
            }
            catch ( e2 : Exception) {
                LogUtil.e( TAG , "err = " + e2.message )
            }
        }
    }

    // 어플리케이션 클래스
    fun getApp() : BrowserApp {
        return (application as BrowserApp)
    }
}