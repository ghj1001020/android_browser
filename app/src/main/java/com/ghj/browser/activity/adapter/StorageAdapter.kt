package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.StorageData

class StorageAdapter(val mContext: Context, val dataList: ArrayList<StorageData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mInflater : LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == StorageType.LOCAL_HEAD.value || viewType == StorageType.SESSION_HEAD.value) {
            val view : View = mInflater.inflate(R.layout.item_history_date, parent, false)
            return HeaderHolder(view)
        }
        else {
            val view : View = mInflater.inflate(R.layout.item_storage, parent, false)
            return StorageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]

        if(holder is HeaderHolder) {
            holder.txtHistoryDate.text = if( data.type == StorageType.LOCAL_HEAD ) { "Local Storage"} else { "Session Storage"}
        }
        else if(holder is StorageHolder){
            holder.layoutStorage.visibility = if(data.type == StorageType.EMPTY) {View.GONE} else {View.VISIBLE}
            holder.noData.visibility = if(data.type == StorageType.EMPTY) {View.VISIBLE} else {View.GONE}

            if(data.type == StorageType.STORAGE) {
                holder.txtKey.text = data.key
                holder.txtValue.text = data.value
                holder.divider.visibility = if(position == dataList.size-1 || dataList[position+1].type == StorageType.SESSION_HEAD)
                                                { View.GONE }
                                            else { View.VISIBLE }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type.value
    }


    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHistoryDate : TextView = itemView.findViewById(R.id.txtHistoryDate)
        val imgOpen : ImageView = itemView.findViewById(R.id.imgOpen)

        init {
            imgOpen.visibility = View.GONE
        }
    }

    class StorageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutStorage : ConstraintLayout = itemView.findViewById(R.id.layoutStorage)
        val txtKey : TextView = itemView.findViewById(R.id.txtKey)
        val txtValue : TextView = itemView.findViewById(R.id.txtValue)
        val divider : View = itemView.findViewById(R.id.divider)
        val noData : TextView = itemView.findViewById(R.id.noData)
    }
}

enum class StorageType(val value: Int) {
    LOCAL_HEAD(0) ,
    SESSION_HEAD(1) ,
    STORAGE(2) ,
    EMPTY(3)
}