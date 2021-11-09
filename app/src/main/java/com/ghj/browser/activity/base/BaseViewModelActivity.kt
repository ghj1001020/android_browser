package com.ghj.browser.activity.base

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ghj.browser.activity.viewmodel.BaseViewModel

abstract class BaseViewModelActivity<VM: BaseViewModel> : BaseActivity() {

    var mViewModel : VM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newViewModel()
        registBaseObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeBaseObserver()
    }

    abstract fun newViewModel() : VM

    fun getViewModel() : VM {
        if(mViewModel==null) {
            mViewModel = newViewModel()
        }
        return mViewModel as VM
    }

    open fun setNetworkObserver(data: Any) {}    // 네트워크 통신 후 데이터변경 옵저버


    private fun registBaseObserver() {
        mViewModel?.networkLiveData?.observe(this, baseNetworkObserver)
    }

    private fun removeBaseObserver() {
        mViewModel?.networkLiveData?.removeObserver(baseNetworkObserver)
    }

    val baseNetworkObserver : Observer<Any> = Observer {
        setNetworkObserver(it)
    }
}