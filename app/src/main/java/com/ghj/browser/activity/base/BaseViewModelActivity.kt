package com.ghj.browser.activity.base

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ghj.browser.activity.viewmodel.BaseViewModel

abstract class BaseViewModelActivity<VM: BaseViewModel> : BaseActivity() {

    lateinit var mViewModel : VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setViewModel()
        registBaseObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeBaseObserver()
    }

    abstract fun setViewModel()
    open fun setNetworkObserver(data: Any) {}    // 네트워크 통신 후 데이터변경 옵저버


    private fun registBaseObserver() {
        mViewModel.networkLiveData.observe(this, baseNetworkObserver)
    }

    private fun removeBaseObserver() {
        mViewModel.networkLiveData.removeObserver(baseNetworkObserver)
    }

    val baseNetworkObserver : Observer<Any> = Observer {
        setNetworkObserver(it)
    }
}