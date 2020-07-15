package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.ToolbarMoreDialogCallback
import kotlinx.android.synthetic.main.toolbar_main_more.*

class ToolbarMoreDialog( var mContext: Context,
                         var dialogId: Int,
                         var callback: ToolbarMoreDialogCallback? ) : Dialog(mContext) , View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        setContentView( R.layout.toolbar_main_more )

        updateLayout()
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
            it.gravity = Gravity.BOTTOM
            window?.attributes = it
        }
    }

    fun updateLayout() {
        btn_more_cookie.setOnClickListener( this )
    }

    override fun onClick(p0: View?) {
        dismiss()

        when( p0?.id ) {
            R.id.btn_more_cookie -> {
                callback?.invoke( this , dialogId , DefineCode.MORE_MENU_COOKIE )
            }
        }
    }
}