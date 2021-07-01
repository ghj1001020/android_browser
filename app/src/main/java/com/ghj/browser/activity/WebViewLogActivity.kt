package com.ghj.browser.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.WebViewLogViewModel

class WebViewLogActivity : BaseViewModelActivity<WebViewLogViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_log)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateAfter() {

    }

    override fun newViewModel(): WebViewLogViewModel? {
        return ViewModelProvider(this).get(WebViewLogViewModel::class.java)
    }
}