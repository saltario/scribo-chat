package com.saltario.scribo.ui.screens.changes

import com.saltario.scribo.R
import com.saltario.scribo.database.USER
import com.saltario.scribo.database.setBioToDatabase
import com.saltario.scribo.utilits.hideNavBottom
import kotlinx.android.synthetic.main.fragment_change_bio.*
import kotlinx.android.synthetic.main.fragment_register.*

class EditProfileFragment : BaseChangeFragment(R.layout.fragment_edit_profile) {

    override fun onStart() {
        super.onStart()

        hideNavBottom()
        initListeners()
    }

    private fun initListeners() {

    }

}