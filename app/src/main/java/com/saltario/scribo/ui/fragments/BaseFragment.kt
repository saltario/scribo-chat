package com.saltario.scribo.ui.fragments

import androidx.fragment.app.Fragment
import com.saltario.scribo.MainActivity
import com.saltario.scribo.utilits.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout){

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }

    override fun onStop() {
        super.onStop()

        APP_ACTIVITY.mAppDrawer.enableDrawer()
    }
}