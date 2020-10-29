package com.ghj.browser.activity

import android.os.Bundle
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity

public class HistoryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_history )
    }

    override fun onCreateAfter() {

    }
}