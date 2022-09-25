package com.ghj.browser.activity.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.activity.adapter.data.WebSiteData
import com.ghj.browser.common.WebSiteType
import com.ghj.browser.common.JobMode
import com.ghj.browser.util.Util


class WebSiteAdapter(val context: Context, val data : ArrayList<HistoryData>, val listener : IWebSiteListener ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface IWebSiteListener {
        fun onDateClick( position: Int )
        fun onUrlClick( position: Int )
        fun onUrlChecked( position: Int, isCheck: Boolean)
    }

    val mInflater: LayoutInflater = LayoutInflater.from(context)
    var jobMode : JobMode = JobMode.VIEW
        set(value) {
            for ( item in data ) {
                item.isSelected = false
            }
            field = value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 날짜
        if( viewType == WebSiteType.DATE.value ) {
            val view : View = mInflater.inflate( R.layout.item_history_date , parent , false )
            return HistoryDateHolder( view )
        }
        // 방문한사이트
        else if( viewType == WebSiteType.URL.value ) {
            val view : View = mInflater.inflate( R.layout.item_history , parent , false )
            return HistoryHolder( view )
        }
        else {
            val height = Util.convertDpToPixcel(context, 56f)
            val view : View = View(context)
            view.layoutParams = ViewGroup.LayoutParams(0, height)
            return EmptyHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data : HistoryData = data.get(position)

        // 날짜 항목
        if( holder is HistoryDateHolder ) {
            val dayOfWeek = getDisplayDayOfWeek( Util.getDayOfWeekFromDateString(data.date, "yyyyMMdd") )
            holder.txtHistoryDate.setText( "${Util.convertDateFormat(data.date, "yyyyMMdd", "yyyy-MM-dd")} ($dayOfWeek)" )

            if( data.isOpen ) {
                holder.imgOpen.setImageResource(R.drawable.ic_arrow_u)
            }
            else {
                holder.imgOpen.setImageResource(R.drawable.ic_arrow_d)
            }

            // 날짜 클릭
            holder.btnHistoryDate.setOnClickListener {v: View? ->
                listener.onDateClick(position)
            }
        }
        // URL 항목
        else if( holder is HistoryHolder ) {
            if( jobMode != JobMode.VIEW) {
                holder.chkSelect.visibility = View.VISIBLE
                holder.chkSelect.isChecked = data.isSelected
            }
            else {
                holder.chkSelect.visibility = View.GONE
            }

            // 배경
            val prevType = if( position-1 >= 0 && this.data.get(position-1).type == WebSiteType.URL ) {
                WebSiteType.URL
            }
            else {
                WebSiteType.DATE
            }
            val nextType = if( position+1 < itemCount && this.data.get(position+1).type == WebSiteType.URL ) {
                WebSiteType.URL
            }
            else {
                WebSiteType.DATE
            }
            if( prevType==WebSiteType.DATE && nextType==WebSiteType.DATE ) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff)
            }
            else if( prevType==WebSiteType.DATE && nextType==WebSiteType.URL) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_t)
            }
            else if( prevType==WebSiteType.URL && nextType==WebSiteType.URL ) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_m)
            }
            else {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_b)
            }

            // 하단 디바이더
            if( nextType == WebSiteType.DATE ) {
                holder.divider.visibility = View.GONE
            }
            else {
                holder.divider.visibility = View.VISIBLE
            }

            holder.txtHistoryDate.setText( Util.convertDateFormat(data.date, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss") )
            holder.txtHistoryTitle.setText( data.title )
            holder.txtHistoryUrl.setText( data.url )
            Util.stringToBitmap(data.icon)?.let {
                holder.imgHistoryIcon.setImageBitmap( it )
            }

            // 아이템 클릭
            holder.layoutItemHistory.setOnClickListener { v: View? ->
                if( jobMode == JobMode.VIEW ) {
                    listener.onUrlClick( position )
                }
                else {
                    data.isSelected = !holder.chkSelect.isChecked
                    holder.chkSelect.isChecked = data.isSelected
                    listener.onUrlChecked( position, data.isSelected )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data.get(position).type.ordinal
    }

    // 날짜 뷰홀더
    class HistoryDateHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val txtHistoryDate : TextView = view.findViewById(R.id.txtHistoryDate)
        val imgOpen : ImageView = view.findViewById(R.id.imgOpen)
        val btnHistoryDate : ConstraintLayout = view.findViewById(R.id.btnHistoryDate)
    }

    // 방문한사이트 뷰홀더
    class HistoryHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val layoutItemHistory : ConstraintLayout = view.findViewById(R.id.layoutItemHistory)
        val imgHistoryIcon : ImageView = view.findViewById(R.id.imgHistoryIcon)
        val txtHistoryDate : TextView = view.findViewById(R.id.txtHistoryDate)
        val txtHistoryTitle : TextView = view.findViewById(R.id.txtHistoryTitle)
        val txtHistoryUrl : TextView = view.findViewById(R.id.txtHistoryUrl)
        val divider : View = view.findViewById(R.id.divider)
        val chkSelect : CheckBox = view.findViewById(R.id.chkSelect)
    }

    class EmptyHolder(val view: View) : RecyclerView.ViewHolder(view) {}

    private fun getDisplayDayOfWeek( dayOfWeek: Int ) : String {
        when( dayOfWeek ) {
            1-> return "일"
            2-> return "월"
            3-> return "화"
            4-> return "수"
            5-> return "목"
            6-> return "금"
            7-> return "토"
            else -> return ""
        }
    }
}