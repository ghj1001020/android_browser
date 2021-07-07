package com.ghj.browser.common

import android.os.Parcel
import android.os.Parcelable

class LargeString(var text : String = "") : Parcelable {


    constructor(parcel: Parcel) : this(parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LargeString> {
        override fun createFromParcel(parcel: Parcel): LargeString {
            return LargeString(parcel)
        }

        override fun newArray(size: Int): Array<LargeString?> {
            return arrayOfNulls(size)
        }
    }
}