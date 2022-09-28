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
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.common.IClickListener
import com.ghj.browser.util.Util

class WebViewLogAdapter(val mContext: Context, val dataList : ArrayList<WebViewLogData>, val listener: IClickListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mInflater : LayoutInflater = LayoutInflater.from(mContext)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == WebkitLogViewType.URL.ordinal) {
            val view : View = mInflater.inflate(R.layout.item_history_date, parent, false)
            return WebViewDateHolder(view)
        }
        else {
            val view : View = mInflater.inflate(R.layout.item_webview_log, parent, false)
            return WebViewLogHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data : WebViewLogData = dataList.get(position)

        if( holder is WebViewDateHolder ) {
            holder.txtHistoryDate.text = data.url
            holder.imgOpen.setImageResource( if(data.isOpen){R.drawable.ic_arrow_u} else{R.drawable.ic_arrow_d} )
            holder.btnHistoryDate.setOnClickListener { view: View? ->
                data.isOpen = !data.isOpen
                listener.onItemClick(position, data.url)
            }
        }
        else if( holder is WebViewLogHolder ) {
            val padding16 = Util.convertDpToPixcel(mContext, 19f)
            val paddingB = if(position == dataList.size-1 || (position < dataList.size-1 && dataList[position+1].type == WebkitLogViewType.URL)) {
                padding16
            } else {
                0
            }
            holder.itemRoot.setPadding(0, padding16, 0, paddingB)
            holder.txtDate.text = Util.convertDateFormat(data.date, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")
            holder.txtFunction.text = data.function
            holder.txtParameter.text = data.params
            holder.txtDesc.text = data.description
        }
    }


    class WebViewDateHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtHistoryDate : TextView = view.findViewById(R.id.txtHistoryDate)
        val imgOpen : ImageView = view.findViewById(R.id.imgOpen)
        val btnHistoryDate : ConstraintLayout = view.findViewById(R.id.btnHistoryDate)
    }

    class WebViewLogHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemRoot : LinearLayout = view.findViewById(R.id.itemRoot)
        val txtDate : TextView = view.findViewById(R.id.txtDate)
        val txtFunction : TextView = view.findViewById(R.id.txtFunction)
        val txtParameter : TextView = view.findViewById(R.id.txtParameter)
        val txtDesc : TextView = view.findViewById(R.id.txtDesc)
    }
}

enum class WebkitLogType(val value: Int) {
    WEBVIEW_START(1) ,
    WEBVIEW_REDIRECT1(2) ,
    WEBVIEW_REDIRECT2(3) ,
    WEBVIEW_FINISH(4) ,
    WEBVIEW_ERROR1(5) ,
    WEBVIEW_ERROR2(6) ,
    WEBVIEW_HTTP_ERROR(7),
    WEBVIEW_SSL(8)
}

enum class WebkitLogViewType(val value: Int) {
    URL(0) ,
    LOG(1)
}