package com.ghj.browser.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.BookmarkAdapter
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.BookmarkViewModel
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.IClickListener
import com.ghj.browser.common.JobMode
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.dialog.CommonDialog
import com.ghj.browser.util.AlertUtil
import kotlinx.android.synthetic.main.activity_bookmark.*
import kotlinx.android.synthetic.main.appbar_bookmark.*

class BookmarkActivity : BaseViewModelActivity<BookmarkViewModel>(), View.OnClickListener {

    private val TAG : String = "BookmarkActivity"

    val bookmarkDatas : ArrayList<BookmarkData> = arrayListOf()
    lateinit var bookmarkAdapter: BookmarkAdapter
    var isChanged : Boolean = false // 즐겨찾기 목록이 변경되었는지 여부


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        initLayout()
        getViewModel().addObserver(this, bookmarkDataObserver, bookmarkDataDeleteObserver)
    }

    fun initLayout() {
        btnBack.setOnClickListener( this )
        btnMore.setOnClickListener( this )
        btnDoDelete.setOnClickListener( this )

        bookmarkAdapter = BookmarkAdapter(this, bookmarkDatas, object: IClickListener{
            override fun onItemClick(position: Int, url: String) {
                // 메인으로 이동
                val intent = Intent(this@BookmarkActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(DefineCode.IT_PARAM.LOAD_URL, url)
                startActivity(intent)
            }
        })
        rvBookmark.adapter = bookmarkAdapter
    }

    override fun onDestroy() {
        getViewModel().removeObserver(bookmarkDataObserver, bookmarkDataDeleteObserver)
        super.onDestroy()
    }

    override fun newViewModel(): BookmarkViewModel {
        return ViewModelProvider(this).get(BookmarkViewModel::class.java)
    }

    override fun onCreateAfter() {
        getViewModel().queryBookmarkData(this)
    }

    // 즐겨찾기 목록조회 옵저버
    val bookmarkDataObserver : Observer<ArrayList<BookmarkData>> = Observer { list: ArrayList<BookmarkData> ->
        bookmarkDatas.clear()
        bookmarkDatas.addAll(list)
        bookmarkAdapter.notifyDataSetChanged()
    }

    // 즐겨찾기 삭제결과 옵저버
    val bookmarkDataDeleteObserver : Observer<Boolean> = Observer { isSuccess: Boolean ->
        if( isSuccess ) {
            getViewModel().queryBookmarkData(this)
            isChanged = true
        }
    }

    override fun onBackPressed() {
        if( bookmarkAdapter.jobMode != JobMode.VIEW ) {
            changeJobMode(JobMode.VIEW)
            return
        }

        val intent = Intent()
        intent.putExtra(DefineCode.IT_PARAM.IS_CHANGED, isChanged)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            // 뒤로가기
            R.id.btnBack -> {
                onBackPressed()
            }
            // 더보기
            R.id.btnMore -> {
                val items = listOf(getString(R.string.delete), getString(R.string.delete_all_bookmark))
                val morePopup = AlertUtil.list(this, anchorView, items ) {position: Int ->
                    // 삭제
                    if( position== 0) {
                        changeJobMode(JobMode.DELETE)
                    }
                    // 전체삭제
                    else if( position == 1 ) {
                        deleteBookmarkAll()
                    }
                }
                morePopup.show()
            }
            // 삭제
            R.id.btnDoDelete -> {
                deleteBookmark()
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

    // 즐겨찾기 선택후 삭제
    fun deleteBookmark() {
        val params : ArrayList<String> = arrayListOf()
        for( item in bookmarkDatas ) {
            if( item.isSelected ) {
                params.add(item.url)
            }
        }

        getViewModel()?.queryBookmarkDataDelete(this, params)
        changeJobMode(JobMode.VIEW)
    }

    // 즐겨찾기 모두 삭제
    fun deleteBookmarkAll() {
        val buttons = arrayOf(getString(R.string.cancel), getString(R.string.ok))
        val dialog = CommonDialog( this , 0 , getString(R.string.confirm_delete_all) , buttons, true, TAG ){ dialog, dialogId, selected, data ->
            if( selected == DefineCode.BTN_RIGHT ) {
                getViewModel().queryBookmarkDataDeleteAll(this)
            }
        }
        dialog.show()
    }
}