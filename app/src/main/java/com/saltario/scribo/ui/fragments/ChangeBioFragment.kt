package com.saltario.scribo.ui.fragments

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

        REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_BIO)
            .setValue(newBio).addOnCompleteListener {
                if (it.isSuccessful){
                    showToast(getString(R.string.app_toast_data_update))
                    USER.bio = newBio
                    fragmentManager?.popBackStack()
                }
            }
    }
}