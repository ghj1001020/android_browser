package com.ghj.browser.activity.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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

    abstract fun onCreateAfter();   // 앱 실행 후 기본적인 체크 후 액티비티 로직 실행
}