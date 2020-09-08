package com.ghj.browser

import androidx.multidex.MultiDexApplication

class BrowserApp : MultiDexApplication() {

    private val TAG : String = "BrowserApp";


    companion object {
        public var isMobile : Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
    }
}