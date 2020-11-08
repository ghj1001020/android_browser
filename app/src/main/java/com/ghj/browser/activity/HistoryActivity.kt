package com.ghj.browser.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.HistoryAdapter
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.util.CommonUtil
import com.ghj.browser.util.JsonUtil
import com.ghj.browser.util.PreferenceHistoryUtil
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.appbar_history.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

public class HistoryActivity : BaseActivity() {

    var actionBar : ActionBar? = null
    var history : ArrayList<String> = ArrayList()

    lateinit var historyAdapter : HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_history )

        initData()
        initLayout()
    }

    override fun onCreateAfter() {
        val dateFormat = SimpleDateFormat( "yyyyMMdd" , Locale.getDefault() )
        val today = dateFormat.format( Date() )
        val yesterday = dateFormat.format( CommonUtil.getDateDifference( Date() , Calendar.DATE , -1 ) )

        var todayCnt = 0
        var yesterdayCnt = 0

        for ( item in history.iterator() ) {
            val history : HistoryData = JsonUtil.parseJson( item , HistoryData::class.java ) ?: continue
            if( today.equals( history.date ) ) {
                todayCnt++
            }
            else if( yesterday.equals( history.date ) ) {
                yesterdayCnt++
            }
            else {
                break
            }
        }

        txt_toolbar_sub_desc1.text = getString( R.string.toolbar_more_history_desc1 , todayCnt )
        txt_toolbar_sub_desc2.text = getString( R.string.toolbar_more_history_desc2 , yesterdayCnt )
    }

    fun initData() {
        history = PreferenceHistoryUtil.getInstance( this ).getWebPageHistory()
        history.reverse()
    }

    fun initLayout() {
        setSupportActionBar(appbar_history)
        actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayShowCustomEnabled( true )  // 커스터마이징 하기 위해 필요
            it.setDisplayHomeAsUpEnabled( false )  // 뒤로가기 버튼 표시여부, 디폴트로 true만 해도 백버튼이 생김
            it.setDisplayShowTitleEnabled( false )   // 액션바에 표시되는 제목의 표시여부
            it.setDisplayShowHomeEnabled( false )   // 홈 아이콘 표시여부
        }

        list_history.layoutManager = LinearLayoutManager( this , RecyclerView.VERTICAL, false )
        historyAdapter = HistoryAdapter( history )
        list_history.adapter = historyAdapter
    }
}