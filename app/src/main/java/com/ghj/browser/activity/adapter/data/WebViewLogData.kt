package com.ghj.browser.activity.adapter.data

import com.ghj.browser.activity.adapter.WebkitLogViewType
import com.google.gson.annotations.Expose

data class WebViewLogData(@Expose val type: WebkitLogViewType,
                          @Expose val insertTime: String,
                          @Expose val url: String,
                          @Expose val date: String,
                          @Expose val function: String,
                          @Expose val params: String,
                          @Expose val description: String ) {

    var isOpen : Boolean = false

    constructor(insertTime: String, url: String)
            : this(WebkitLogViewType.URL, insertTime, url, "", "", "", """""")

    constructor(insertTime: String, url: String, date: String, function: String, params: String, description: String)
            : this(WebkitLogViewType.LOG, insertTime, url, date, function, params, description)

}
