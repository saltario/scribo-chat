package com.saltario.scribo.ui.fragments.changes

import com.saltario.scribo.R
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.*
import kotlinx.android.synthetic.main.fragment_change_username.*
import java.util.*

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()

        settings_input_username.setText(USER.username)
    }

    override fun change() {
        super.change()

        mNewUsername = settings_input_username.text.toString().lowercase(Locale.getDefault())

        if (mNewUsername.isEmpty()){
            showToast(getString(R.string.settings_toast_username_is_empty))

        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(AppValueEventListener {
                if (it.hasChild(mNewUsername)){
                    showToast(getString(R.string.settings_toast_username_not_unique))
                } else {
                    setUsernameToDatabase(mNewUsername)
                }
            })
        }
    }
}