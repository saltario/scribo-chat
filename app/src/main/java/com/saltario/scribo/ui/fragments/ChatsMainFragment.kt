package com.saltario.scribo.ui.fragments

import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.utilits.APP_ACTIVITY

class ChatsMainFragment : Fragment(R.layout.fragment_chats) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Чаты"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
    }
}