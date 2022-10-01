package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R
import com.ghj.browser.common.CookieAddDialogCallback
import com.ghj.browser.util.NetworkUtil
import kotlinx.android.synthetic.main.dialog_cookie_add.*

class CookieAddDialog( var mContext : Context , val domain: String, val callback : CookieAddDialogCallback ) : Dialog(mContext),
    View.OnClickListener, DialogInterface.OnDismissListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        setContentView( R.layout.dialog_cookie_add )

        initLayout()
    }

    fun initLayout() {
        setOnDismissListener(this)
        btnCancel.setOnClickListener(this)
        btnAdd.setOnClickListener(this)
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

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.btnCancel -> {
                dismiss()
            }
            R.id.btnAdd -> {
                val key = editKey.text.toString()
                val value = editValue.text.toString()
                NetworkUtil.addCookie(domain, key, value)

                editKey.setText("")
                editValue.setText("")
            }
        }
    }

    override fun onDismiss(p0: DialogInterface?) {
        callback(this)
    }
}