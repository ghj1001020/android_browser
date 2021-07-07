package com.ghj.browser.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ghj.browser.R
import com.ghj.browser.common.ScriptInputDialogCallback
import com.ghj.browser.util.Util
import kotlinx.android.synthetic.main.dialog_script_input.*

class ScriptInputDialog(val mContext: Context, val callback: ScriptInputDialogCallback) : Dialog(mContext), View.OnClickListener {

    init {
        window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        window?.requestFeature( Window.FEATURE_NO_TITLE )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        setContentView(R.layout.dialog_script_input)
        initLayout()

        setCancelable( false )
        setCanceledOnTouchOutside( false )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val width = Util.getDisplayWidth(mContext)
        window?.setLayout((0.86*width).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun initLayout() {
        btnCancel.setOnClickListener(this)
        btnOk.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when( v?.id ) {
            // 취소
            R.id.btnCancel -> {
                dismiss()
            }
            // 확인
            R.id.btnOk -> {
                dismiss()
                callback(this, etScript.text.toString())
            }
        }
    }

}