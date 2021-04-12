package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.common.HistoryType
import com.ghj.browser.util.Util
import com.ghj.browser.util.JsonUtil


class HistoryAdapter( val context: Context, val datas : ArrayList<HistoryData>, val listener : IHistoryListener ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface IHistoryListener {
        fun onDateClick( position: Int )
        fun onUrlClick( position: Int )
    }

    val mInflater: LayoutInflater = LayoutInflater.from(context)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 날짜
        if( viewType == HistoryType.DATE.value ) {
            val view : View = mInflater.inflate( R.layout.item_history_date , parent , false )
            return HistoryDateHolder( view )
        }
        // 방문한사이트
        else {
            val view : View = mInflater.inflate( R.layout.item_history , parent , false )
            return HistoryHolder( view )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data : HistoryData = datas.get(position)

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
            // 배경
            val prevType = if( position-1 >= 0 && datas.get(position-1).type == HistoryType.URL ) {
                HistoryType.URL
            }
            else {
                HistoryType.DATE
            }
            val nextType = if( position+1 < itemCount && datas.get(position+1).type == HistoryType.URL ) {
                HistoryType.URL
            }
            else {
                HistoryType.DATE
            }
            if( prevType==HistoryType.DATE && nextType==HistoryType.DATE ) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff)
            }
            else if( prevType==HistoryType.DATE && nextType==HistoryType.URL) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_t)
            }
            else if( prevType==HistoryType.URL && nextType==HistoryType.URL ) {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_m)
            }
            else {
                holder.layoutItemHistory.setBackgroundResource(R.drawable.rip_bg_r28_fff_b)
            }

            // 하단 디바이더
            if( nextType == HistoryType.DATE ) {
                holder.divider.visibility = View.GONE
            }
            else {
                holder.divider.visibility = View.VISIBLE
            }

            holder.txtHistoryTitle.setText( data.title )
            holder.txtHistoryUrl.setText( data.url )
            Util.stringToBitmap(data.icon)?.let {
                holder.imgHistoryIcon.setImageBitmap( it )
            }

            // 사이트 클릭
            holder.layoutItemHistory.setOnClickListener { v: View? ->
                listener.onUrlClick( position )
            }
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        return datas.get(position).type.ordinal
    }

    // 날짜 뷰홀더
    class HistoryDateHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val txtHistoryDate : TextView = view.findViewById(R.id.txtHistoryDate)
        val imgOpen : ImageView = view.findViewById(R.id.imgOpen)
        val btnHistoryDate : ConstraintLayout = view.findViewById(R.id.btnHistoryDate)
    }

    // 방문한사이트 뷰홀더
    class HistoryHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val layoutItemHistory : LinearLayout = view.findViewById(R.id.layoutItemHistory)
        val imgHistoryIcon : ImageView = view.findViewById(R.id.imgHistoryIcon)
        val txtHistoryTitle : TextView = view.findViewById(R.id.txtHistoryTitle)
        val txtHistoryUrl : TextView = view.findViewById(R.id.txtHistoryUrl)
        val divider : View = view.findViewById(R.id.divider)
    }

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