package com.ghj.browser.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.WebSiteAdapter
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.activity.base.BaseViewModelActivity
import com.ghj.browser.activity.viewmodel.HistoryViewModel
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.WebSiteType
import com.ghj.browser.common.JobMode
import com.ghj.browser.db.SQLiteService
import com.ghj.browser.util.Util
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.appbar_history.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : BaseViewModelActivity<HistoryViewModel>(), View.OnClickListener {

    var webSiteDatas : ArrayList<WebSiteData> = ArrayList()
    lateinit var webSiteAdapter : WebSiteAdapter

    // 더보기 > 삭제
    lateinit var popupMenu : PopupMenu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_history )

        initLayout()
    }

    override fun newViewModel() : HistoryViewModel {
        return ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateAfter() {
        queryHistoryData()
        queryHistoryCnt()
    }

    fun initLayout() {
        btn_back.setOnClickListener(this)
        btnShare.setOnClickListener(this)
        btnSearch.setOnClickListener(this)
        btnMore.setOnClickListener(this)
        btnDoDelete.setOnClickListener(this)
        btnDoShare.setOnClickListener(this)

        // 히스토리 목록
        webSiteAdapter = WebSiteAdapter( this, webSiteDatas, webSiteListener )
        rvHistory.adapter = webSiteAdapter

        // 더보기 > 삭제
        popupMenu = PopupMenu(this, btnMore)
        popupMenu.menuInflater.inflate(R.menu.delete_more, popupMenu.menu)
        if( Build.VERSION_CODES.M <= Build.VERSION.SDK_INT ) {
            popupMenu.gravity = Gravity.END
        }
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            when( item?.itemId ) {
                R.id.delete-> {
                    changeJobMode(JobMode.DELETE)
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

    override fun onBackPressed() {
        if( webSiteAdapter.jobMode != JobMode.VIEW ) {
            changeJobMode(JobMode.VIEW)
            return
        }
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // 앱바 > 뒤로가기
            R.id.btn_back->{
                onBackPressed()
            }

            // 앱바 > 공유
            R.id.btnShare->{
                changeJobMode(JobMode.SHARE)
            }

            // 앱바 > 검색
            R.id.btnSearch->{
                val intent: Intent = Intent(this, SearchActivity::class.java)
                startActivityForResult(intent, DefineCode.ACT_REQ_ID.SEARCH)
            }

            // 앱바 > 더보기
            R.id.btnMore->{
                popupMenu.show()
            }

            // 항목선택 > 삭제
            R.id.btnDoDelete->{
                deleteHistory()
            }

            // 항목선택 > 공유
            R.id.btnDoShare->{
                shareHistory()
            }
        }
    }

    // 히스토리 목록 데이터 조회
    fun queryHistoryData() {
        // 히스토리 날짜, URL목록
        webSiteDatas.clear()
        val list : ArrayList<WebSiteData> = SQLiteService.selectHistoryDatesAndUrls(this)
        webSiteDatas.addAll(list)
        webSiteAdapter.notifyDataSetChanged()
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
    val webSiteListener : WebSiteAdapter.IWebSiteListener = object : WebSiteAdapter.IWebSiteListener {
        override fun onDateClick( position: Int ) {
            val date = webSiteDatas.get(position).date.substring(0, 8)
            webSiteDatas.get(position).isOpen = !webSiteDatas.get(position).isOpen

            if( webSiteDatas.get(position).isOpen ) {
                val urls = SQLiteService.selectHistoryUrlsByDate(this@HistoryActivity, date)
                webSiteDatas.addAll(position+1, urls)
                webSiteAdapter.notifyDataSetChanged()
            }
            else {
                val removeDatas = arrayListOf<WebSiteData>()
                for( item in webSiteDatas ) {
                    if( item.type == WebSiteType.URL && item.date.startsWith(date) ) {
                        removeDatas.add(item)
                    }
                }
                webSiteDatas.removeAll(removeDatas)
                webSiteAdapter.notifyDataSetChanged()
            }
        }

        override fun onUrlClick( position: Int ) {
            val url : String = webSiteDatas.get(position).url
            moveUrl(url)
        }
    }

    // 메인에 이동할 URL 전달하고 액티비티 종료
    fun moveUrl( url: String ) {
        val intent = Intent()
        intent.putExtra( DefineCode.IT_PARAM.HISTORY_URL , url )
        setResult( Activity.RESULT_OK, intent)
        finish()
    }

    // 전체 히스토리 목록 삭제
    fun deleteHistoryAll() {
        SQLiteService.deleteHistoryDataAll(this)
        queryHistoryData()
        queryHistoryCnt()
    }

    // 히스토리 목록 삭제
    fun deleteHistory() {
        val params : ArrayList<String> = arrayListOf()
        for( item in webSiteDatas ) {
            if( item.isSelected ) {
                params.add(item.date)
            }
        }

        SQLiteService.deleteHistoryData(this, params)
        changeJobMode(JobMode.VIEW)
        queryHistoryData()
        queryHistoryCnt()
    }

    // 히스토리 목록 공유
    fun shareHistory() {
        val str : StringBuilder = StringBuilder()
        for( item in webSiteDatas ) {
            if( item.isSelected ) {
                str.append(item.title + "\n")
                str.append(item.url + "\n")
            }
        }

        val intent: Intent = Intent(Intent.ACTION_SEND)
        intent.flags =Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, str.toString())
        startActivity(Intent.createChooser(intent, ""))
    }

    // 작업 타입변경
    fun changeJobMode( mode: JobMode ) {
        // 목록조회
        if( mode == JobMode.VIEW ) {
            val prevJob = webSiteAdapter.jobMode
            if( prevJob == JobMode.DELETE ) {
                layoutDelete.visibility = View.GONE
                layoutDelete.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_bottom)
            }
            else if( prevJob == JobMode.SHARE ) {
                layoutShare.visibility = View.GONE
                layoutShare.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_bottom)
            }

            webSiteAdapter.jobMode = JobMode.VIEW
            webSiteAdapter.notifyDataSetChanged()
        }
        // 아이템 삭제
        else if( mode == JobMode.DELETE ) {
            layoutDelete.visibility = View.VISIBLE
            layoutDelete.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_bottom)
            webSiteAdapter.jobMode = JobMode.DELETE
            webSiteAdapter.notifyDataSetChanged()
        }
        // 아이템 공유
        else if( mode == JobMode.SHARE ) {
            layoutShare.visibility = View.VISIBLE
            layoutShare.animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_bottom)
            webSiteAdapter.jobMode = JobMode.SHARE
            webSiteAdapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 검색
        if( requestCode == DefineCode.ACT_REQ_ID.SEARCH ) {
            if( resultCode == Activity.RESULT_OK ) {
                if( data == null ) return

                val url = data.getStringExtra(DefineCode.IT_PARAM.HISTORY_URL) ?: ""
                moveUrl(url)
            }
        }
    }
}