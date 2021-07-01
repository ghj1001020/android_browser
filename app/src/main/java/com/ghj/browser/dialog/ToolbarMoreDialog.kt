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
import com.ghj.browser.BrowserApp
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.ghj.browser.common.ToolbarMoreDialogCallback
import kotlinx.android.synthetic.main.dialog_toolbar_more.*

class ToolbarMoreDialog( var mContext: Context,
                         var dialogId: Int,
                         var callback: ToolbarMoreDialogCallback? ) : Dialog(mContext) , View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        setContentView( R.layout.dialog_toolbar_more )

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
        btn_more_printer.setOnClickListener( this )
        btn_more_pcm_mode.setOnClickListener( this )
        btn_more_history.setOnClickListener( this )
        btn_more_console.setOnClickListener( this )
        btn_more_webview_log.setOnClickListener( this )
        btn_more_js_execute.setOnClickListener( this )
        btn_more_html_element.setOnClickListener( this )

        if( BrowserApp.isMobile ) {
            txt_more_pcm_mode.text = mContext.getString( R.string.toolbar_more_pc_mode )
            img_more_pcm_mode.setImageResource( R.drawable.ic_toolbar_desktop )
        }
        else {
            txt_more_pcm_mode.text = mContext.getString( R.string.toolbar_more_mobile_mode )
            img_more_pcm_mode.setImageResource( R.drawable.ic_toolbar_mobile )
        }
    }

    override fun onClick(p0: View?) {
        dismiss()

        when( p0?.id ) {
            R.id.btn_more_cookie -> {
                callback?.invoke( this , dialogId , DefineCode.MORE_MENU_COOKIE )
            }

            R.id.btn_more_printer -> {
                callback?.invoke( this , dialogId , DefineCode.MORE_MENU_PRINTER )
            }

            R.id.btn_more_pcm_mode -> {
                callback?.invoke( this , dialogId , DefineCode.MORE_MENU_PCM_MODE )
            }

            R.id.btn_more_history -> {
                callback?.invoke( this , dialogId , DefineCode.MORE_MENU_HISTORY )
            }

            R.id.btn_more_console -> {
                callback?.invoke(this, dialogId, DefineCode.MORE_MENU_CONSOLE)
            }

            R.id.btn_more_webview_log -> {
                callback?.invoke(this, dialogId, DefineCode.MORE_MENU_WEBVIEW_LOG)
            }

            R.id.btn_more_js_execute -> {
                callback?.invoke(this, dialogId, DefineCode.MORE_MENU_JS_EXECUTE)
            }

            R.id.btn_more_html_element -> {
                callback?.invoke(this, dialogId, DefineCode.MORE_MENU_HTML_ELEMENT)
            }
        }
    }
}