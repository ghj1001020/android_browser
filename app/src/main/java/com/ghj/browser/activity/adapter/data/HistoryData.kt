package com.ghj.browser.activity.adapter.data

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import android.webkit.WebBackForwardList
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoryData( @Expose var date : String, @Expose var icon : String, @Expose var title : String, @Expose var url : String ) : Parcelable
