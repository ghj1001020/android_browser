package com.ghj.browser.activity

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.BookmarkAdapter
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.BookmarkViewModel
import com.ghj.browser.common.JobMode
import com.ghj.browser.util.AlertUtil
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.appbar_bookmark.*

class BookmarkActivity : BaseViewModelActivity<BookmarkViewModel>(), View.OnClickListener {

    val bookmarkDatas : ArrayList<BookmarkData> = arrayListOf()
    lateinit var bookmarkAdapter: BookmarkAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        initLayout()
        getViewModel()?.addObserver(this, bookmarkDataObserver)
    }

    fun initLayout() {
        btnBack.setOnClickListener( this )
        btnMore.setOnClickListener( this )

        bookmarkAdapter = BookmarkAdapter(this, bookmarkDatas)
        rvBookmark.adapter = bookmarkAdapter
    }

    override fun onDestroy() {
        getViewModel()?.removeObserver(bookmarkDataObserver)
        super.onDestroy()
    }

    override fun newViewModel(): BookmarkViewModel? {
        return ViewModelProvider(this).get(BookmarkViewModel::class.java)
    }

    override fun onCreateAfter() {
        getViewModel()?.queryBookmarkData(this)
    }

    // 즐겨찾기 목록조회 옵저버
    val bookmarkDataObserver : Observer<ArrayList<BookmarkData>> = Observer { list: ArrayList<BookmarkData> ->
        bookmarkDatas.clear()
        bookmarkDatas.addAll(list)
        bookmarkAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if( bookmarkAdapter.jobMode != JobMode.VIEW ) {
            changeJobMode(JobMode.VIEW)
            return
        }
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            // 뒤로가기
            R.id.btnBack -> {
                finish()
            }
            // 더보기
            R.id.btnMore -> {
                val items = listOf(getString(R.string.delete), getString(R.string.delete_all_bookmark))
                val morePopup = AlertUtil.list(this, anchorView, items ) {position: Int ->
                    // 삭제
                    if( position== 1) {
                        changeJobMode(JobMode.DELETE)
                    }
                    // 전체삭제
                    else if( position == 2 ) {

                    }
                }
                morePopup.show()
            }
        }
    }

    // 작업모드 변경
    fun changeJobMode(mode: JobMode) {
        if( mode == JobMode.VIEW ) {
            layoutDelete.visibility = View.GONE
            layoutDelete.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_bottom)
        }
        else if( mode == JobMode.DELETE ) {
            layoutDelete.visibility = View.VISIBLE
            layoutDelete.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_bottom)
        }
        bookmarkAdapter.jobMode = mode
        bookmarkAdapter.notifyDataSetChanged()
    }
}