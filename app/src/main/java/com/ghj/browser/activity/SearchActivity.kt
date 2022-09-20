package com.ghj.browser.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.WebSiteAdapter
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import com.ghj.browser.db.SQLiteService
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.appbar_search.*

class SearchActivity : BaseActivity(), View.OnClickListener {

    lateinit var searchAdapter : WebSiteAdapter
    var searchDatas : ArrayList<HistoryData> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initLayout()
    }

    override fun onCreateAfter() {

    }

    fun initLayout() {
        btnBack.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        etSearch.doAfterTextChanged {
            if( TextUtils.isEmpty(it.toString()) ) {
                btnDelete.visibility = View.GONE
                searchDatas.clear()
                searchAdapter.notifyDataSetChanged()
            }
            else {
                btnDelete.visibility = View.VISIBLE
                val list : ArrayList<HistoryData> = SQLiteService.selectHistorySearch(this, it.toString().trim())
                searchDatas.clear()
                searchDatas.addAll(list)
                searchAdapter.notifyDataSetChanged()
            }
        }

        // 검색 목록
        searchAdapter = WebSiteAdapter(this, searchDatas, searchListener)
        rvSearch.adapter = searchAdapter
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            // 뒤로가기
            R.id.btnBack-> {
                finish()
            }

            // 검색어 글자 삭제
            R.id.btnDelete-> {
                etSearch.setText("")
            }
        }
    }

    val searchListener : WebSiteAdapter.IWebSiteListener = object : WebSiteAdapter.IWebSiteListener {
        override fun onDateClick(position: Int) {

        }

        override fun onUrlClick(position: Int) {
            val intent = Intent()
            intent.putExtra(DefineCode.IT_PARAM.HISTORY_URL, searchDatas.get(position).url)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}