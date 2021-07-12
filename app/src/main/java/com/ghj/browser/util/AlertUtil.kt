package com.ghj.browser.util

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import com.ghj.browser.R

object AlertUtil {

    // 목록형태 팝업
    fun list( context: Context, anchorView: View, items: List<String>, callback: ((position:Int)->Unit)? ) : ListPopupWindow {
        val popupWindow = ListPopupWindow(context)
        popupWindow.anchorView = anchorView

        val adapter = ArrayAdapter(context, R.layout.common_item, R.id.txtItem, items)
        popupWindow.setAdapter(adapter)
        popupWindow.setOnItemClickListener() { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            popupWindow.dismiss()
            if (callback != null) {
                callback(position)
            }
        }
        return popupWindow
    }
}