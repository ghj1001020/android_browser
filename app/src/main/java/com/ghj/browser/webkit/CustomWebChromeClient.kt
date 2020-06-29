package com.ghj.browser.webkit

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.ghj.browser.common.DefineCode
import com.ghj.browser.dialog.CommonDialog

class CustomWebChromeClient : WebChromeClient {

    private val TAG = "CustomWebChromeClient"

    private var context : Context? = null
    private var listener : OnWebViewListener? = null

    // dialog
    private var dialog : CommonDialog? = null


    constructor( context: Context? , listener: OnWebViewListener? ) : super() {
        this.context = context
        this.listener = listener
    }


    // 페이지 타이틀
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)

        view?.let {
            listener?.onReceivedTitle( it, title ?: "" )
        }
    }

    // 페이지 로딩 정도
    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        view?.let {
            listener?.onProgressChanged( it, newProgress )
        }
    }

    // alert
    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        if( context == null || (context is Activity && (context as Activity).isFinishing) ) {
            dialog?.dismiss()
            result?.cancel()
            return true
        }

        try {
            if( dialog?.isShowing ?: false ) {
                dialog?.dismiss()
            }

            dialog = CommonDialog( context as Context , DefineCode.DIALOG_ID_JS_ALERT , TAG , object : CommonDialog.OnCommonDialogListener{
                override fun onCommonPopupClick(dialog: Dialog, dialogId: Int, selected: Int, data: String?) {
                    dialog.dismiss()

                    if( selected == DefineCode.BTN_OK ) {
                        result?.confirm()
                    }
                }
            })

            dialog?.show()
            return true
        }
        catch ( e : Exception ) {
            return false
        }
    }

    // confirm
    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        if( context == null || (context is Activity && (context as Activity).isFinishing) ) {
            dialog?.dismiss()
            result?.cancel()
            return true
        }

        try {
            if( dialog?.isShowing ?: false ) {
                dialog?.dismiss()
            }

            dialog = CommonDialog( context as Context , DefineCode.DIALOG_ID_JS_CONFIRM , TAG , object : CommonDialog.OnCommonDialogListener{
                override fun onCommonPopupClick(dialog: Dialog, dialogId: Int, selected: Int, data: String?) {
                    dialog.dismiss()

                    if( selected == DefineCode.BTN_LEFT ) {
                        result?.cancel()
                    }
                    else if( selected == DefineCode.BTN_RIGHT ) {
                        result?.confirm()
                    }
                }
            })

            dialog?.show()
            return true
        }
        catch ( e : Exception ) {
            return false
        }
    }
}