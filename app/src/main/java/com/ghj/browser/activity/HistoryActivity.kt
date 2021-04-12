package com.ghj.browser.activity

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.HistoryAdapter
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.ui.adapter.ListPopupAdapter
import com.ghj.browser.activity.viewmodel.HistoryViewModel
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.HistoryType
import com.ghj.browser.common.IClickListener
import com.ghj.browser.db.SQLite
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.util.Util
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.appbar_history.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

public class HistoryActivity : BaseViewModelActivity<HistoryViewModel>(), View.OnClickListener {

    var actionBar : ActionBar? = null
    var historyDatas : ArrayList<HistoryData> = ArrayList()

    lateinit var historyAdapter : HistoryAdapter

    // 더보기 > 삭제
    lateinit var popupMenu : PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_history )

        initLayout()
    }

    override fun setViewModel() {
        mViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateAfter() {
        queryHistoryData()
        queryHistoryCnt()
    }

    fun initData() {
        queryHistoryData()
    }

    fun initLayout() {
        btn_back.setOnClickListener(this)
        btnShare.setOnClickListener(this)
        btnSearch.setOnClickListener(this)
        btnMore.setOnClickListener(this)

        // 히스토리 목록
        historyAdapter = HistoryAdapter( this, historyDatas, historyListener )
        rvHistory.adapter = historyAdapter

        // 더보기 > 삭제
        popupMenu = PopupMenu(this, btnMore)
        popupMenu.menuInflater.inflate(R.menu.delete_more, popupMenu.menu)
        if( Build.VERSION_CODES.M <= Build.VERSION.SDK_INT ) {
            popupMenu.gravity = Gravity.END
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            when( item?.itemId ) {
                R.id.delete-> {
                    true
                }
                R.id.deleteAll-> {
                    deleteHistoryAll()
                    true
                }
                else-> {
                    false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // 뒤로가기
            R.id.btn_back->{
                finish()
            }

            // 공유
            R.id.btnShare->{

            }

            // 검색
            R.id.btnSearch->{

            }

            // 더보기
            R.id.btnMore->{
                popupMenu.show()
            }
        }
    }

    // 히스토리 목록 데이터 조회
    fun queryHistoryData() {
        // 히스토리 날짜, URL목록
        historyDatas.clear()
        val historyData : ArrayList<HistoryData> = SQLiteService.selectHistoryDatesAndUrls(this)
        historyDatas.addAll(historyData)
        historyAdapter.notifyDataSetChanged()
    }

    // 오늘, 어제 히스토리 데이터 개수
    fun queryHistoryCnt() {
        val dateFormat = SimpleDateFormat( "yyyyMMdd" , Locale.getDefault() )
        val today = dateFormat.format( Date() )
        val yesterday = dateFormat.format( Util.getDateDifference( Date() , Calendar.DATE , -1 ) )

        val todayCnt = SQLiteService.selectHistoryCntByDate(this, today)
        val yesterdayCnt = SQLiteService.selectHistoryCntByDate(this, yesterday)

        // 어제, 오늘 방문사이트 개수
        txt_toolbar_sub_desc1.text = getString( R.string.toolbar_more_history_desc1 , todayCnt )
        txt_toolbar_sub_desc2.text = getString( R.string.toolbar_more_history_desc2 , yesterdayCnt )
    }

    // 히스토리 목록 클릭 리스너
    val historyListener : HistoryAdapter.IHistoryListener = object : HistoryAdapter.IHistoryListener {
        override fun onDateClick( position: Int ) {
            val date = historyDatas.get(position).date.substring(0, 8)
            historyDatas.get(position).isOpen = !historyDatas.get(position).isOpen

            if( historyDatas.get(position).isOpen ) {
                val urls = SQLiteService.selectHistoryUrlsByDate(this@HistoryActivity, date)
                historyDatas.addAll(position+1, urls)
                historyAdapter.notifyDataSetChanged()
            }
            else {
                val removeDatas = arrayListOf<HistoryData>()
                for( item in historyDatas ) {
                    if( item.type == HistoryType.URL && item.date.startsWith(date) ) {
                        removeDatas.add(item)
                    }
                }
                historyDatas.removeAll(removeDatas)
                historyAdapter.notifyDataSetChanged()
            }
        }

        override fun onUrlClick( position: Int ) {
            val url : String = historyDatas.get(position).url
            val intent = Intent()
            intent.putExtra( DefineCode.IT_PARAM.HISTORY_URL , url )
            setResult( Activity.RESULT_OK, intent)
            finish()
        }
    }

    // 전체 히스토리 목록 삭제
    fun deleteHistoryAll() {
        SQLiteService.deleteHistoryDataAll(this)
        queryHistoryData()
        queryHistoryCnt()
    }
}