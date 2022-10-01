package com.ghj.browser.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.LogUtil
import kotlinx.android.synthetic.main.activity_index.*

public class IndexActivity : BaseActivity() , View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_index )

        initLayout()
    }

    override fun onCreateAfter() {

    }

    fun initLayout() {
        edit_index_url.setOnEditorActionListener { v, actionId, event ->
            if( actionId == EditorInfo.IME_ACTION_SEARCH ) {
                indexPageSearch()
                true
            }
            else {
                false
            }
        };
        btn_index_search.setOnClickListener( this )
        btnTestPage.setOnClickListener( this )
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_index_search -> {
                indexPageSearch()
            }
            R.id.btnTestPage -> {
                moveToMain(DefineCode.TEST_PAGE)
            }
        }
    }

    // 인덱스페이지 > 검색
    fun indexPageSearch() {
        var search = edit_index_url.text.toString()
        if( TextUtils.isEmpty( search ) ) {
            search = DefineCode.DEFAULT_PAGE
        }

        moveToMain(search)
    }

    // 메인화면으로 이동
    fun moveToMain(search: String) {
        val intent = Intent( this , MainActivity::class.java )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra( DefineCode.IT_PARAM.LOAD_URL , search )
        startActivity( intent )
    }
}