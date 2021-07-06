package com.ghj.browser.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.WebViewLogAdapter
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.WebViewLogViewModel
import kotlinx.android.synthetic.main.activity_webview_log.*
import kotlinx.android.synthetic.main.appbar_common.*

class WebViewLogActivity : BaseViewModelActivity<WebViewLogViewModel>(), View.OnClickListener {

    val webViewLogDatas : ArrayList<WebViewLogData> = ArrayList()
    lateinit var webViewLogAdapter : WebViewLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_log)

        initLayout()
        getViewModel()?.addObserver(this, webViewLogDataListObserver, webViewLogDataDeleteAllObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewModel()?.removeObserver(webViewLogDataListObserver, webViewLogDataDeleteAllObserver)
    }

    fun initLayout() {
        toolbarTitle.text = getString(R.string.toolbar_more_webview)
        btnBack.setOnClickListener( this )
        btnDelete.setOnClickListener( this )

        webViewLogAdapter = WebViewLogAdapter(this, webViewLogDatas)
        rvWebViewLog.adapter = webViewLogAdapter
    }

    override fun onCreateAfter() {
        getViewModel()?.queryWebViewLogData(this)
    }

    override fun newViewModel(): WebViewLogViewModel? {
        return ViewModelProvider(this).get(WebViewLogViewModel::class.java)
    }

    // 웹뷰로그 데이터 목록조회 옵저버
    val webViewLogDataListObserver : Observer<ArrayList<WebViewLogData>> = Observer { list : ArrayList<WebViewLogData> ->
        webViewLogDatas.clear()
        webViewLogDatas.addAll(list)
        webViewLogAdapter.notifyDataSetChanged()
    }

    // 웹뷰로그 데이터 삭제결과 옵저버
    val webViewLogDataDeleteAllObserver : Observer<Boolean> = Observer { isSuccess: Boolean ->
        if( isSuccess ) {
            getViewModel()?.queryWebViewLogData(this)
        }
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            // 뒤로가기
            R.id.btnBack -> {
                finish()
            }
            // 전체삭제
            R.id.btnDelete -> {
                getViewModel()?.queryWebViewLogDataDeleteAll(this)
            }
        }
    }
}
