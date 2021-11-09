package com.ghj.browser

import androidx.multidex.MultiDexApplication

class BrowserApp : MultiDexApplication() {

    companion object {
        public var isMobile : Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
    }
}