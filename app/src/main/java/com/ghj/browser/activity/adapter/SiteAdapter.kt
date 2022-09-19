package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R

class SiteAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var siteMode : SiteMode = SiteMode.BOOKMARK
    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = mInflater.inflate(R.layout.item_site, parent, false)
        return SiteHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {

    }


    class SiteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle : TextView = itemView.findViewById(R.id.txtTitle)
        val txtUrl : TextView = itemView.findViewById(R.id.txtUrl)
        val divider : View = itemView.findViewById(R.id.divider)
    }
}

enum class SiteMode(val value: Int) {
    BOOKMARK(0),    // 북마크
    HISTORY(1)  // 히스토리
}
