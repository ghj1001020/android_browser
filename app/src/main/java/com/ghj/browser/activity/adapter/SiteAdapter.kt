package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.activity.adapter.data.WebSiteData

class SiteAdapter(val context: Context,
                  val bookmarkList: ArrayList<BookmarkData>,
                  val historyList: ArrayList<WebSiteData>) : RecyclerView.Adapter<SiteAdapter.SiteHolder>() {

    var siteMode : SiteMode = SiteMode.BOOKMARK
    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteHolder {
        val view : View = mInflater.inflate(R.layout.item_site, parent, false)
        return SiteHolder(view)
    }

    override fun onBindViewHolder(holder: SiteHolder, position: Int) {
        if(siteMode == SiteMode.BOOKMARK) {
            val data : BookmarkData = bookmarkList[position]
            holder.txtTitle.text = data.title
            holder.txtUrl.text = data.url
            holder.divider.visibility = if(position == bookmarkList.size-1) { View.GONE } else { View.VISIBLE }
        }
        else {
            val data : WebSiteData = historyList[position]
            holder.txtTitle.text = data.title
            holder.txtUrl.text = data.url
            holder.divider.visibility = if(position == bookmarkList.size-1) { View.GONE } else { View.VISIBLE }
        }
    }

    override fun getItemCount(): Int {
        if(siteMode == SiteMode.BOOKMARK) {
            return bookmarkList.size
        }
        else {
            return historyList.size
        }
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
