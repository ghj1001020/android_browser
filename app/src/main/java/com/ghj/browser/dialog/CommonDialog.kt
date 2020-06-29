package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R

class CommonDialog : Dialog {

    var TAG : String = "CommonDialog"

    // 팝업 클릭 인터페이스
    interface OnCommonDialogListener {
        fun onCommonPopupClick( dialog : Dialog , dialogId : Int , selected : Int , data : String? )
    }

    var mContext : Context? = null
    var dialogId : Int = -1
    var title : String? = null
    var contents : String? = null
    var buttonTexts : Array<String>? = null
    var isCancel : Boolean = false
    var listener : OnCommonDialogListener? = null
    var data : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        setContentView( R.layout.dialog_common )

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
            window?.attributes = it
        }
    }

    constructor( context: Context , dialogId: Int , tag : String , listener : OnCommonDialogListener? , data : String? = "" ) : super(context) {
        this.mContext = context
        if( !TextUtils.isEmpty( TAG ) ) {
            this.TAG = tag
        }
        this.dialogId = dialogId
        this.listener = listener
        this.data = data
    }

    fun updateLayout() {

    }

    override fun show() {
        if( isShowing ) {
            dismiss()
        }

        super.show()
    }
}