package com.saltario.scribo.ui.fragments

import com.saltario.scribo.R
import com.saltario.scribo.utilits.APP_ACTIVITY

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
    }

}