package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.WebViewLogData
import com.ghj.browser.util.Util

class WebViewLogAdapter(val mContext: Context, val dataList : List<WebViewLogData>) : RecyclerView.Adapter<WebViewLogAdapter.WebViewLogHolder>() {

    val mInflater : LayoutInflater = LayoutInflater.from(mContext)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebViewLogHolder {
        val view : View = mInflater.inflate(R.layout.item_webview_log, parent, false)
        return WebViewLogHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: WebViewLogHolder, position: Int) {
        val data : WebViewLogData = dataList.get(position)
        holder.txtDate.text = Util.convertDateFormat(data.date, "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss")
        holder.txtFunction.text = data.function
        holder.txtParameter.text = data.params
        holder.txtDesc.text = data.description
    }

    class WebViewLogHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDate : TextView = view.findViewById(R.id.txtDate)
        val txtFunction : TextView = view.findViewById(R.id.txtFunction)
        val txtParameter : TextView = view.findViewById(R.id.txtParameter)
        val txtDesc : TextView = view.findViewById(R.id.txtDesc)
    }
}