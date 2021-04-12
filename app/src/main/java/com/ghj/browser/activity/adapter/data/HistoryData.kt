package com.ghj.browser.activity.adapter.data

import android.os.Parcelable
import com.ghj.browser.common.HistoryType
import kotlinx.android.parcel.Parcelize


@Parcelize
data class HistoryData(val type: HistoryType, val date : String, val title : String, val url : String, val icon : String ) : Parcelable {

    var isOpen : Boolean = false

    constructor( type: HistoryType, date: String ) : this(type, date, "", "", "")
}