package com.ghj.browser.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.ConsoleAdapter
import com.ghj.browser.activity.adapter.data.ConsoleData
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.ConsoleViewModel
import kotlinx.android.synthetic.main.activity_console.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.appbar_common.*

class ConsoleActivity : BaseViewModelActivity<ConsoleViewModel>(), View.OnClickListener {

    val consoleDatas : ArrayList<ConsoleData> = ArrayList()
    lateinit var consoleAdapter : ConsoleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)

        initLayout()
        getViewModel()?.addObserver(this, consoleLogDataObserver, consoleLogDataDeleteAllObserver)
    }

    override fun onDestroy() {
        getViewModel()?.removeObserver(consoleLogDataObserver, consoleLogDataDeleteAllObserver)
        super.onDestroy()
    }

    fun initLayout() {
        btnBack.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        toolbarTitle.text = getString(R.string.toolbar_more_console)

        consoleAdapter = ConsoleAdapter(this, consoleDatas)
        rvConsoleLog.adapter = consoleAdapter
    }

    override fun onCreateAfter() {
        getViewModel()?.queryConsoleLogData(this)
    }

    override fun newViewModel() : ConsoleViewModel {
        return ViewModelProvider(this).get(ConsoleViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // 뒤로가기
            R.id.btnBack-> {
                finish()
            }

            // 모두지우기
            R.id.btnDelete-> {
                deleteAllConsoleLog()
            }
        }
    }

    // 콘솔로그 데이터 옵저버
    val consoleLogDataObserver : Observer<ArrayList<ConsoleData>> = Observer { list: ArrayList<ConsoleData> ->
        consoleDatas.clear()
        consoleDatas.addAll( list )
        consoleAdapter.notifyDataSetChanged()
    }

    // 콘솔로그 모든 데이터 삭제 옵저버
    val consoleLogDataDeleteAllObserver : Observer<Boolean> = Observer { isSuccess: Boolean ->
        if(isSuccess) {
            getViewModel()?.queryConsoleLogData(this)
        }
    }

    // 콘솔로그 모두 지우기
    fun deleteAllConsoleLog() {
        getViewModel()?.queryConsoleLogDataDeleteAll(this)
    }
}