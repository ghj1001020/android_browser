package com.ghj.browser.webkit

import com.google.gson.annotations.Expose

data class JsAlertPopupData(@Expose val title: String, @Expose val message: String )

data class JsGetMessageData(@Expose val title: String, @Expose val message: String )