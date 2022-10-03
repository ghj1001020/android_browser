package com.ghj.browser.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ghj.browser.R
import com.ghj.browser.activity.adapter.StorageAdapter
import com.ghj.browser.activity.adapter.StorageType
import com.ghj.browser.activity.adapter.data.StorageData
import com.ghj.browser.activity.base.BaseActivity
import com.ghj.browser.common.DefineCode
import com.ghj.browser.util.LogUtil
import kotlinx.android.synthetic.main.activity_storage.*
import kotlinx.android.synthetic.main.appbar_common.*
import org.json.JSONObject
import java.lang.Exception

class StorageActivity : BaseActivity(), View.OnClickListener {

    val storageList : ArrayList<StorageData> = arrayListOf()
    lateinit var storageAdapter : StorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        initData()
        initLayout()
    }

    fun initData() {
        val strLocal = intent.getStringExtra(DefineCode.IT_PARAM.LOCAL_STORAGE) ?: ""
        val strSession = intent.getStringExtra(DefineCode.IT_PARAM.SESSION_STORAGE) ?: ""
        LogUtil.d(strLocal)
        LogUtil.d(strSession)

        storageList.add(StorageData(StorageType.LOCAL_HEAD))
        val localData : ArrayList<StorageData> = arrayListOf()
        try {
            val jsonLocal = JSONObject(strLocal)
            for( key in jsonLocal.keys()) {
                val value = "${jsonLocal[key]}"
                localData.add( StorageData(StorageType.STORAGE, key, value) )
            }
        }
        catch (e: Exception) {
            LogUtil.e(e.message)
            if(!strLocal.isEmpty()) {
                localData.add( StorageData(StorageType.STORAGE, "", strLocal) )
            }
        }
        if(localData.size == 0) {
            storageList.add(StorageData(StorageType.EMPTY))
        }
        else {
            storageList.addAll(localData)
        }

        storageList.add(StorageData(StorageType.SESSION_HEAD))
        val sessionData : ArrayList<StorageData> = arrayListOf()
        try {
            val jsonSession = JSONObject(strSession)
            for( key in jsonSession.keys()) {
                val value = "${jsonSession[key]}"
                sessionData.add( StorageData(StorageType.STORAGE, key, value) )
            }
        }
        catch (e: Exception) {
            LogUtil.e(e.message)
            if(!strSession.isEmpty()) {
                localData.add( StorageData(StorageType.STORAGE, "", strSession) )
            }
        }
        if(sessionData.size == 0) {
            storageList.add(StorageData(StorageType.EMPTY))
        }
        else {
            storageList.addAll(sessionData)
        }
    }

    fun initLayout() {
        btnBack.setOnClickListener(this)
        btnDelete.visibility = View.GONE
        toolbarTitle.text = getString(R.string.toolbar_more_storage)

        storageAdapter = StorageAdapter(this, storageList)
        rvStorage.adapter = storageAdapter
    }

    override fun onCreateAfter() {
        storageAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnBack -> {
                finish()
            }
        }
    }
}