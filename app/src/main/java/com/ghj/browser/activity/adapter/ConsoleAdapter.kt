package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.ConsoleData
import com.ghj.browser.util.Util
import kotlinx.android.synthetic.main.item_console.view.*

class ConsoleAdapter( val mContext: Context, var dataList: List<ConsoleData> ) : RecyclerView.Adapter<ConsoleAdapter.ConsoleHolder>() {

    val mInflater : LayoutInflater = LayoutInflater.from(mContext)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsoleHolder {
        val view = mInflater.inflate(R.layout.item_console, parent, false)
        return ConsoleHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ConsoleHolder, position: Int) {
        val data : ConsoleData = dataList.get(position)

        holder.txtDate.text = Util.convertDateFormat(data.date, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")
        holder.txtLog.text = data.log

        val params : ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dp12 = Util.convertDpToPixcel(mContext, 12.0f)
        if( position == dataList.size-1 ) {
            params.setMargins(dp12, dp12, dp12, dp12)
        }
        else {
            params.setMargins(dp12, dp12, dp12, 0)
        }
        holder.rootItem.layoutParams = params
    }


    class ConsoleHolder( val view: View ) : RecyclerView.ViewHolder(view) {
        val rootItem : ConstraintLayout = view.findViewById(R.id.rootItem)
        val txtDate : TextView = view.findViewById(R.id.txtDate)
        val txtLog : TextView = view.findViewById(R.id.txtLog)
    }
}