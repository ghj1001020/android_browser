package com.ghj.browser.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.WebViewLogAdapter
import com.ghj.browser.activity.adapter.WebkitLogViewType
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.WebViewLogViewModel
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.IClickListener
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.dialog.CommonDialog
import com.ghj.browser.util.JsonUtil
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_webview_log.*
import kotlinx.android.synthetic.main.appbar_common.*
import org.json.JSONObject
import java.io.Serializable

class WebViewLogActivity : BaseViewModelActivity<WebViewLogViewModel>(), View.OnClickListener {

    private val TAG: String = "WebViewLogActivity"

    val webViewLogDatas : ArrayList<WebViewLogData> = ArrayList()
    lateinit var webViewLogAdapter : WebViewLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_log)

        initLayout()
        getViewModel().addObserver(this, webViewLogGroupListObserver, webViewLogDataListObserver, webViewLogDataDeleteAllObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        getViewModel().removeObserver(webViewLogGroupListObserver, webViewLogDataListObserver, webViewLogDataDeleteAllObserver)
    }

    fun initLayout() {
        toolbarTitle.text = getString(R.string.toolbar_more_webview)
        btnBack.setOnClickListener( this )
        btnDelete.setOnClickListener( this )

        webViewLogAdapter = WebViewLogAdapter(this, webViewLogDatas, object : IClickListener {
            override fun onItemClick(position: Int, url: String) {
                val item = webViewLogDatas[position]
                if(item.isOpen) {
//                    val list : ArrayList<WebViewLogData> = SQLiteService.selectWebViewLogsByUrl(mContext, item.insertTime, item.url)
//                    webViewLogDatas.addAll(position+1, list)
                    getViewModel().queryWebViewLogDataByUrl(mContext, position, item)
                }
                else {
                    val removeData = arrayListOf<WebViewLogData>()
                    for(index in position+1 until webViewLogDatas.size) {
                        if(webViewLogDatas[index].type == WebkitLogViewType.LOG) {
                            removeData.add(webViewLogDatas[index])
                        }
                        else break
                    }
                    webViewLogDatas.removeAll(removeData)
                }
                webViewLogAdapter.notifyDataSetChanged()
            }
        })
        rvWebViewLog.adapter = webViewLogAdapter
    }

    override fun onCreateAfter() {
        getViewModel()?.queryWebViewLogData(this)
    }

    override fun newViewModel(): WebViewLogViewModel {
        return ViewModelProvider(this).get(WebViewLogViewModel::class.java)
    }

    // 웹뷰로그 데이터 그룹조회 옵저버
    val webViewLogGroupListObserver : Observer<ArrayList<WebViewLogData>> = Observer { list : ArrayList<WebViewLogData> ->
        webViewLogDatas.clear()
        webViewLogDatas.addAll(list)
        webViewLogAdapter.notifyDataSetChanged()
    }

    // 웹뷰로그 데이터 목록조회 옵저버
    val webViewLogDataListObserver : Observer<JSONObject> = Observer { json: JSONObject ->
        val position : Int = json.getInt("position")
        val jsonString : String = json.getString("list")


        val list : ArrayList<WebViewLogData>? = JsonUtil.parseArray(jsonString, object: TypeToken<ArrayList<WebViewLogData>>(){}.type)
        if(list != null) {
            webViewLogDatas.addAll(position+1, list)
        }
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
                val buttons = arrayOf(getString(R.string.cancel), getString(R.string.ok))
                val dialog = CommonDialog( this , 0 , getString(R.string.confirm_delete_all) , buttons, true, TAG ){ dialog, dialogId, selected, data ->
                    if( selected == DefineCode.BTN_RIGHT ) {
                        getViewModel().queryWebViewLogDataDeleteAll(this)
                    }
                }
                dialog.show()
            }
        }
    }
}
