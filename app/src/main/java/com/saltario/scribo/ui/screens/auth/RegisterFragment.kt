package com.saltario.scribo.ui.screens.auth

import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.database.*
import com.saltario.scribo.ui.objects.AppValueEventListener
import com.saltario.scribo.utilits.hideNavBottom
import com.saltario.scribo.utilits.restartActivity
import com.saltario.scribo.utilits.showToast
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*

class RegisterFragment(val uid: String) : Fragment(R.layout.fragment_register) {

    override fun onStart() {
        super.onStart()

        hideNavBottom()
        initListeners()
    }

    private fun initListeners() {
        back_btn.setOnClickListener { parentFragmentManager.popBackStack() }
        register_btn_done.setOnClickListener { done() }
    }

    private fun done() {

        changeUserName()
        changeFullName()

        restartActivity()
    }

    private fun changeUserName() {

        val username = register_input_username.text.toString().lowercase(Locale.getDefault())

        if (username.isEmpty()){
            showToast(getString(R.string.settings_toast_username_is_empty))

        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(
                AppValueEventListener {
                if (it.hasChild(username)){
                    showToast(getString(R.string.settings_toast_username_not_unique))
                } else {
                    setUsernameToDatabase(username, uid)
                }
            })
        }
    }

    private fun changeFullName() {

        val name = register_input_name.text.toString()
        val surname = register_input_surname.text.toString()

        if (name.isEmpty()){
            showToast(getString(R.string.settings_toast_name_is_empty))

        } else {
            val fullname = "$name $surname"
            setFullNameToDatabase(fullname, uid)
        }
    }

}