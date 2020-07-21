package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R
import com.ghj.browser.common.CookieAddDialogCallback

class CookieAddDialog( var mContext : Context , callback : CookieAddDialogCallback ) : Dialog(mContext) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        setContentView( R.layout.dialog_cookie_add )

        updateLayout()
    }


    fun updateLayout() {

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        // 딤처리
        val layoutParams : WindowManager.LayoutParams? = window?.attributes
        layoutParams?.let {
            it.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            it.dimAmount = 0.7f
            it.width = WindowManager.LayoutParams.MATCH_PARENT
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
            window?.attributes = it
        }
    }
}