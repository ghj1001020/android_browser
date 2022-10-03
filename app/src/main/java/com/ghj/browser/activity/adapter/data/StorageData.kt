package com.ghj.browser.activity.adapter.data

import com.ghj.browser.activity.adapter.StorageType

data class StorageData(val type: StorageType, val key: String, val value: String) {

    constructor(type: StorageType) : this(type, "", "")
}
