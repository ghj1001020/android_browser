package com.ghj.browser.activity.adapter.data

import android.os.Parcelable
import com.ghj.browser.common.WebSiteType
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WebSiteData(val type: WebSiteType, val date : String, val title : String, val url : String, val icon : String ) : Parcelable {

    var isOpen : Boolean = false
    var isSelected : Boolean = false    // 아이템 선택여부 (true-선택됨, fasle-선택안됨)

    constructor( type: WebSiteType, date: String ) : this(type, date, "", "", "")
}
