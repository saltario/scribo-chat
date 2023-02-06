package com.saltario.scribo.ui.screens.changes

import com.saltario.scribo.R
import com.saltario.scribo.ui.screens.BaseFragment
import com.saltario.scribo.utilits.hideNavBottom

class EditProfileFragment : BaseFragment(R.layout.fragment_edit_profile) {

    override fun onStart() {
        super.onStart()

        hideNavBottom()
        initListeners()
    }

    private fun initListeners() {

    }

}