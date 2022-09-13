package com.saltario.scribo.ui.fragments.changes

import com.saltario.scribo.R
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_change_bio.*

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()

        settings_input_bio.setText(USER.bio)
    }

    override fun change() {
        super.change()

        val newBio = settings_input_bio.text.toString()
        setBioToDatabase(newBio)
    }
}