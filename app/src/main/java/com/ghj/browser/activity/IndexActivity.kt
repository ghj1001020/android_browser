package com.ghj.browser.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.ghj.browser.R
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
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
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_index_search -> {
                indexPageSearch()
            }
        }
    }

    // 인덱스페이지 > 검색
    fun indexPageSearch() {
        var search = edit_index_url.text.toString()
        if( TextUtils.isEmpty( search ) ) {
            search = DefineCode.DEFAULT_PAGE
        }

        val intent = Intent( this , MainActivity::class.java )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra( DefineCode.IT_PARAM_INDEX_URL , search )
        startActivity( intent )

        edit_index_url?.setText( "" )
    }
}