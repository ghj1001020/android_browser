package com.ghj.browser.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ghj.browser.R
import com.ghj.browser.util.LogUtil


class AlertDialogFragment : DialogFragment {

    interface AlertDialogFragmentInterface {
        fun onClickListener( dialog : AlertDialogFragment , requestId : Int , selected : Int )
    }

    val TAG : String = "AlertDialogFragment"


    var requestId = -1
    var title : String? = null
    var message : String? = null
    var buttons : Array<String>? = null
    var listener : AlertDialogFragmentInterface? = null


    private constructor( buttons : Array<String>? , listener: AlertDialogFragmentInterface? ) : super() {
        this.buttons = buttons
        this.listener = listener
    }

    companion object {

        const val BTN_SELECT_POSITIVE = 1
        const val BTN_SELECT_NEGATIVE = 2
        const val BTN_SELECT_CANCEL = 3

        @JvmStatic fun newInstance( requestId : Int , title : String? , message : String? , isCancel : Boolean , buttons : Array<String>? , listener : AlertDialogFragmentInterface? ) : AlertDialogFragment {
            val dialog = AlertDialogFragment( buttons , listener )

            dialog.arguments = Bundle().apply {
                this.putInt( "ALERT_DIALOG_FRAG_REQ_ID" , requestId )
                this.putString( "ALERT_DIALOG_FRAG_TITLE" , title )
                this.putString( "ALERT_DIALOG_FRAG_MESSAGE" , message )
                this.putBoolean( "ALERT_DIALOG_FRAG_IS_CANCEL" , isCancel )
            }

            return dialog
        }
    }


    override fun onCreateView( inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? ): View?
    {
        dialog?.window?.setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN )
        dialog?.window?.requestFeature( Window.FEATURE_NO_TITLE )
//        dialog?.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT) )

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        this.requestId = arguments?.getInt( "ALERT_DIALOG_FRAG_REQ_ID") ?: -1
        this.title = arguments?.getString( "ALERT_DIALOG_FRAG_TITLE" , "" )
        this.message = arguments?.getString( "ALERT_DIALOG_FRAG_MESSAGE" , "" )
        val isCancel = arguments?.getBoolean( "ALERT_DIALOG_FRAG_IS_CANCEL" , true ) ?: true

        activity?.let {
            setCancelable( isCancel )

            val builder : AlertDialog.Builder = AlertDialog.Builder( it , R.style.CustomAlertDialogFragment )
            builder.setTitle( title )
            builder.setMessage( message )

            var btnPositive : String = getString( R.string.common_ok )
            var btnNegative : String = getString( R.string.common_cancel )

            if( buttons == null || buttons?.size == 0 ) {
                buttons = arrayOf( btnPositive )
            }

            buttons?.let {
                if( it.size == 1 ) {
                    if( !TextUtils.isEmpty( it[0] ) ) btnPositive = it[0]
                }
                else if( it.size == 2 ) {
                    if( !TextUtils.isEmpty( it[0] ) ) btnPositive = it[0]
                    if( !TextUtils.isEmpty( it[1] ) ) btnNegative = it[1]
                }


                // 확인버튼 클릭 리스너
                builder.setPositiveButton( btnPositive) { dialog, which ->
                    dialog.dismiss()

                    listener?.onClickListener( this , requestId , BTN_SELECT_POSITIVE )
                }

                // 취소버튼 클릭 리스너
                if( it.size >= 2 ) {
                    builder.setNegativeButton( btnNegative ) { dialog, which ->
                        dialog.dismiss()

                        listener?.onClickListener( this , requestId , BTN_SELECT_NEGATIVE )
                    }
                }
            }

            return builder.create()
        }

        throw Exception( "[AlertDialogFragment] activity is null" )
    }

    override fun onCancel(dialog: DialogInterface) {
        LogUtil.d( TAG , "onCancel" )
        listener?.onClickListener( this , requestId , BTN_SELECT_CANCEL )
        super.onCancel(dialog)
    }
}