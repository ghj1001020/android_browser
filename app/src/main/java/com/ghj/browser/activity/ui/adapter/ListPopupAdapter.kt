package com.ghj.browser.activity.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ghj.browser.R

class ListPopupAdapter( val context: Context , val datas: List<String> ) : BaseAdapter() {

    val mInflater : LayoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView : View? = view
        var holder : ListPopupHolder

        if( convertView == null ) {
            convertView = mInflater.inflate(R.layout.item_list_popup, parent, false)
            holder = ListPopupHolder()
            holder.txtTitle = convertView.findViewById<TextView>( R.id.txtTitle )
            convertView.tag = holder
        }
        else {
            holder = convertView.tag as ListPopupHolder
        }

        holder.txtTitle?.text = datas.get(position)
        return convertView as View
    }

    override fun getItem(position: Int): Any {
        return datas.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return datas.size
    }


    class ListPopupHolder {
        var txtTitle : TextView? = null
    }
}