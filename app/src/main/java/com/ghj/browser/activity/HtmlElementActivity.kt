package com.ghj.browser.activity

import android.os.Bundle
import android.view.View
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.PrefUtil
import kotlinx.android.synthetic.main.activity_html_element.*
import kotlinx.android.synthetic.main.appbar_common.*

class HtmlElementActivity : BaseActivity(), View.OnClickListener {

    var htmlElement : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html_element)

        initLayout()
    }

    fun initLayout() {
        toolbarTitle.text = getString(R.string.toolbar_more_html_element)
        btnBack.setOnClickListener(this)
        btnDelete.visibility = View.GONE
    }

    override fun onCreateAfter() {
        htmlElement = PrefUtil.getInstance(this).getString(DefineCode.PREF_KEY.HTML_ELEMENT)
        txtElement.setText( htmlElement )
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            R.id.btnBack-> {
                finish()
            }
        }
    }
}