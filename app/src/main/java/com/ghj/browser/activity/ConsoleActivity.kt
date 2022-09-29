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
import com.ghj.browser.common.DefineCode
import kotlinx.android.synthetic.main.activity_console.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.appbar_common.*

class ConsoleActivity : BaseViewModelActivity<ConsoleViewModel>(), View.OnClickListener {

    lateinit var consoleDatas : ArrayList<ConsoleData>
    lateinit var consoleAdapter : ConsoleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_console)

        consoleDatas = intent.getSerializableExtra( DefineCode.IT_PARAM_CONSOLE ) as ArrayList<ConsoleData>
        initLayout()
    }

    fun initLayout() {
        btnBack.setOnClickListener(this)
        btnDelete.visibility = View.GONE
        toolbarTitle.text = getString(R.string.toolbar_more_console)

        consoleAdapter = ConsoleAdapter(this, consoleDatas)
        rvConsoleLog.adapter = consoleAdapter
    }

    override fun onCreateAfter() {
        consoleAdapter.notifyDataSetChanged()
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
        }
    }
}