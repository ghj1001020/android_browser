package com.ghj.browser.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.data.CookieData

class CookieAdapter( var mContext: Context , var cookieData : ArrayList<CookieData> ) : BaseAdapter() {


    override fun getView(position : Int , view : View? , parent : ViewGroup?): View {
        var convertView : View? = view

        if( convertView == null ) {
            val inflater : LayoutInflater = mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater
            convertView = inflater.inflate( R.layout.item_cookie , parent , false )
        }

        val cookieKey = convertView?.findViewById<TextView>( R.id.txt_cookie_key )
        val cookieValue = convertView?.findViewById<TextView>( R.id.txt_cookie_value )
        cookieKey?.text = cookieData.get( position ).key.trim()
        cookieValue?.text = cookieData.get( position ).cookie.trim()

        return convertView as View
    }

    override fun getItem(p0: Int): Any {
        var position = p0
        if( position < 0 ) position = 0
        if( position >= cookieData.size ) position = cookieData.size - 1

        return cookieData.get( position )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return cookieData.size
    }

    // 데이터 추가
    fun addItem( item : CookieData ) {
        cookieData.add( item )
    }

    // 데이터 전부 삭제
    fun clearItem() {
        cookieData.clear()
    }
}