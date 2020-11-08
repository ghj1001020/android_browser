package com.ghj.browser.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebBackForwardList
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.HistoryData
import com.ghj.browser.util.CommonUtil
import com.ghj.browser.util.JsonUtil
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter( val datas : ArrayList<String>? ) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    class HistoryHolder(val view : View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view : View = LayoutInflater.from( parent.context ).inflate( R.layout.item_history , parent , false )
        return HistoryHolder( view )
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        if( datas == null ) {
            return
        }

        val icon = holder.view.findViewById<ImageView>( R.id.img_history_icon )
        val title = holder.view.findViewById<TextView>( R.id.txt_history_title )
        val url = holder.view.findViewById<TextView>( R.id.txt_history_url )
        val divider = holder.view.findViewById<View>( R.id.divider )

        val item = JsonUtil.parseJson( datas.get( position ) , HistoryData::class.java )
        item?.let {
            CommonUtil.stringToBitmap( item.icon )?.let {
                icon.setImageBitmap( it )
            }
            title.text = item.title
            url.text = item.url

            if( position == datas.size-1 ) {
                divider.visibility = View.GONE
            }
            else {
                divider.visibility = View.VISIBLE
            }
        }
    }
}