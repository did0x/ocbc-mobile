package com.putrash.ocbcmobile.arch

import android.os.Bundle
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun setView(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        return
    }
}