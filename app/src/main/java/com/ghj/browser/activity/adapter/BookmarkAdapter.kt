package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.BookmarkData
import com.ghj.browser.common.IClickListener
import com.ghj.browser.common.JobMode
import com.ghj.browser.util.Util

class BookmarkAdapter( val mContext: Context, var dataList: List<BookmarkData>, val listener: IClickListener) : RecyclerView.Adapter<BookmarkAdapter.BookmarkHolder>() {

    val mInflater: LayoutInflater = LayoutInflater.from(mContext)
    var jobMode = JobMode.VIEW
        set(value) {
            for( item in dataList ) {
                item.isSelected = false
            }
            field = value
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkHolder {
        val view: View = mInflater.inflate(R.layout.item_bookmark, parent, false)
        return BookmarkHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: BookmarkHolder, position: Int) {
        val data = dataList.get(position)

        // 선택 박스
        if( jobMode == JobMode.VIEW ) {
            holder.chkSelect.visibility = View.GONE
        }
        else {
            holder.chkSelect.visibility = View.VISIBLE
            holder.chkSelect.isChecked = data.isSelected
        }

        holder.imgFavicon.setImageBitmap(Util.stringToBitmap(data.favicon))
        holder.txtTitle.text = data.title
        holder.txtUrl.text = data.url

        // 배경
        if( itemCount == 1 ) {
            holder.rootItem.setBackgroundResource( R.drawable.rip_bg_r28_fff )
        }
        else {
            if( position == 0 ) {
                holder.rootItem.setBackgroundResource( R.drawable.rip_bg_r28_fff_t )
            }
            else if( position == itemCount-1 ) {
                holder.rootItem.setBackgroundResource( R.drawable.rip_bg_r28_fff_b )
            }
            else {
                holder.rootItem.setBackgroundResource( R.drawable.rip_bg_r28_fff_m )
            }
        }

        // 마지막행 마진
        val params : ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if( position >= dataList.size-1 ) {
            holder.divider.visibility = View.GONE
            params.setMargins(0, 0, 0, Util.convertDpToPixcel(mContext, 16.0f))
        }
        else {
            holder.divider.visibility = View.VISIBLE
            params.setMargins(0, 0, 0, 0)
        }
        holder.rootItem.layoutParams = params

        // 아이템 클릭
        holder.rootItem.setOnClickListener { v: View? ->
            // URL 이동
            if( jobMode == JobMode.VIEW ) {
                listener.onItemClick(position, data.url)
            }
            // 삭제 선택
            else if( jobMode == JobMode.DELETE ) {
                data.isSelected = !data.isSelected
                holder.chkSelect.isChecked = data.isSelected
            }
        }
    }

    class BookmarkHolder(itemView: View ) : RecyclerView.ViewHolder(itemView) {
        val rootItem : ConstraintLayout = itemView.findViewById(R.id.rootItem)
        val chkSelect : AppCompatCheckBox = itemView.findViewById(R.id.chkSelect)
        val imgFavicon : AppCompatImageView = itemView.findViewById(R.id.imgFavicon)
        val txtTitle : TextView = itemView.findViewById(R.id.txtTitle)
        val txtUrl : TextView = itemView.findViewById(R.id.txtUrl)
        val divider : View = itemView.findViewById(R.id.divider)
    }
}