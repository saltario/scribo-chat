package com.saltario.scribo.ui.fragments

import android.view.View
import com.saltario.scribo.R
import com.saltario.scribo.models.Common
import com.saltario.scribo.utilits.APP_ACTIVITY
import kotlinx.android.synthetic.main.activity_main.view.*

class SingleChatFragment(contact: Common) : BaseFragment(R.layout.fragment_single_chat) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mToolBar.infoToolBar.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.mToolBar.infoToolBar.visibility = View.GONE
    }
}