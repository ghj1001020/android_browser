package com.ghj.browser.bottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.ghj.browser.R
import com.ghj.browser.common.DefineCode
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_menu.*

class MenuBottomSheet(context: Context, val callback: MenuClickCallback) : BottomSheetDialog(context, R.style.BaseBottomSheetDialog), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_menu)

        initLayout()
    }

    fun initLayout() {
        history.setOnClickListener(this)
        webkitLog.setOnClickListener(this)
        consoleLog.setOnClickListener(this)
        cookie.setOnClickListener(this)
        scriptExe.setOnClickListener(this)
        htmlSource.setOnClickListener(this)
        pcMobile.setOnClickListener(this)
        printer.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        dismiss()
        when(p0?.id) {
            R.id.history -> {
                callback(this, DefineCode.MORE_MENU_HISTORY)
            }
            R.id.webkitLog -> {
                callback(this, DefineCode.MORE_MENU_WEBVIEW_LOG)
            }
            R.id.consoleLog -> {
                callback(this, DefineCode.MORE_MENU_CONSOLE)
            }
            R.id.cookie -> {
                callback(this, DefineCode.MORE_MENU_COOKIE)
            }
            R.id.scriptExe -> {
                callback(this, DefineCode.MORE_MENU_JS_EXECUTE)
            }
            R.id.htmlSource -> {
                callback(this, DefineCode.MORE_MENU_HTML_ELEMENT)
            }
            R.id.pcMobile -> {
                callback(this, DefineCode.MORE_MENU_PCM_MODE)
            }
            R.id.printer -> {
                callback(this, DefineCode.MORE_MENU_PRINTER)
            }
        }
    }
}

typealias MenuClickCallback = (dialog : Dialog, selected : Int )->Unit
