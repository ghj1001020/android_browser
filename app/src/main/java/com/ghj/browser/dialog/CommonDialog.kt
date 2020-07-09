package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R
import com.ghj.browser.common.CommonDialogCallback
import com.ghj.browser.common.DefineCode
import kotlinx.android.synthetic.main.dialog_common.*


class CommonDialog : Dialog , View.OnClickListener {

    var TAG : String = "CommonDialog"


    var mContext : Context? = null
    var dialogId : Int = -1
    var message : String? = null
    var buttons : Array<String>? = null
    var isCancel : Boolean = false
    var callback : CommonDialogCallback? = null
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

    constructor( context: Context , dialogId: Int , message : String? , buttons : Array<String>? , isCancel : Boolean , tag : String? , callback : CommonDialogCallback? , data : String? ) : super(context) {
        initData( context , dialogId , message , buttons , isCancel , tag , callback , data)
    }

    constructor( context: Context , dialogId: Int , message : String? , buttons : Array<String>? , isCancel : Boolean , tag : String , callback : CommonDialogCallback? ) : super(context) {
        initData( context , dialogId , message , buttons , isCancel , tag , callback , null)
    }

    fun initData( context: Context , dialogId: Int , message : String? , buttons : Array<String>? , isCancel : Boolean , tag : String? , callback : CommonDialogCallback? , data : String? ) {
        this.mContext = context
        if( !TextUtils.isEmpty( tag ) ) {
            this.TAG = tag as String
        }

        this.dialogId = dialogId
        this.message = message
        this.buttons = buttons
        this.isCancel = isCancel
        this.callback = callback
        this.data = data
    }

    fun updateLayout() {
        setCancelable( isCancel )
        setCanceledOnTouchOutside( isCancel )

        txt_dialog_message?.setText( this.message )
        // 버튼
        if( buttons == null || buttons!!.size < 2 ) {
            layout_dialog_btns1?.visibility = View.VISIBLE
            layout_dialog_btns2?.visibility = View.GONE

            var btn_text = mContext?.getString( R.string.common_ok ) ?: "확인"
            buttons?.let {
                if( it.size > 0 ) {
                    btn_text = it[0]
                }
            }
            btn_dialog_ok?.text = btn_text

            btn_dialog_ok?.setOnClickListener( this )
            btn_dialog_left?.setOnClickListener( null )
            btn_dialog_right?.setOnClickListener( null )
        }
        else {
            layout_dialog_btns1?.visibility = View.GONE
            layout_dialog_btns2?.visibility = View.VISIBLE

            var btn_text1 = mContext?.getString( R.string.common_cancel ) ?: "취소"
            var btn_text2 = mContext?.getString( R.string.common_ok ) ?: "확인"
            buttons?.let {
                if( it.size > 1 ) {
                    btn_text1 = it[0]
                    btn_text2 = it[1]
                }
            }
            btn_dialog_left?.text = btn_text1
            btn_dialog_right?.text = btn_text2

            btn_dialog_left?.setOnClickListener( this )
            btn_dialog_right?.setOnClickListener( this )
            btn_dialog_ok?.setOnClickListener( null )
        }
    }

    override fun show() {
        if( isShowing ) {
            dismiss()
        }

        super.show()
    }

    override fun onClick(v: View?) {
        dismiss()

        when( v?.id ) {
            R.id.btn_dialog_ok -> {
                callback?.invoke( this , dialogId , DefineCode.BTN_OK , data )
            }

            R.id.btn_dialog_left -> {
                callback?.invoke( this , dialogId , DefineCode.BTN_LEFT , data )
            }

            R.id.btn_dialog_right -> {
                callback?.invoke( this , dialogId , DefineCode.BTN_RIGHT , data )
            }
        }
    }
}