package com.ghj.browser.activity.adapter.data

import android.os.Parcelable
import com.ghj.browser.common.WebSiteType
import kotlinx.android.parcel.Parcelize


@Parcelize
data class WebSiteData(val date : String, val title : String, val url : String, val icon : String ) : Parcelable
