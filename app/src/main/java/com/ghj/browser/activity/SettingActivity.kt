package com.ghj.browser.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ghj.browser.BrowserApp
import com.ghj.browser.BuildConfig
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.appbar_common.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    var userAgent = ""
    var isMobile = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initData()
        initLayout()
    }

    fun initData() {
        userAgent = intent.getStringExtra(DefineCode.IT_PARAM.USERAGENT) ?: ""
        isMobile = BrowserApp.isMobile
    }

    fun initLayout() {
        btnBack.setOnClickListener(this)
        btnDelete.visibility = View.GONE
        toolbarTitle.text = getString(R.string.toolbar_more_setting)

        txtVersion.text = "v${BuildConfig.VERSION_NAME}"
        txtUserAgent.text = userAgent
        changeIsMobile()

        swiMode.setOnCheckedChangeListener { buttonView, isChecked ->
            isMobile = !isMobile
            changeIsMobile()
        }
    }

    override fun onCreateAfter() {

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnBack -> {
                onFinish()
            }
        }
    }

    override fun onBackPressed() {
        onFinish()
        super.onBackPressed()
    }

    fun changeIsMobile() {
        if(isMobile) {
            txtMode.text = "ON"
            swiMode.isChecked = true
            userAgent = userAgent.replace( "droidA" , "Android" ).replace( "obileM" , "Mobile" )
        }
        else {
            txtMode.text = "OFF (PC)"
            swiMode.isChecked = false
            userAgent = userAgent.replace( "Android" , "droidA" ).replace( "Mobile" , "obileM" )
        }
        txtUserAgent.text = userAgent
    }

    fun onFinish() {
        val intent = Intent()
        intent.putExtra(DefineCode.IT_PARAM.IS_CHANGED, !(isMobile == BrowserApp.isMobile))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}