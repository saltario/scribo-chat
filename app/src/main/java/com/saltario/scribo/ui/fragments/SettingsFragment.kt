package com.saltario.scribo.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.saltario.scribo.MainActivity
import com.saltario.scribo.R
import com.saltario.scribo.activities.RegisterActivity
import com.saltario.scribo.utilits.AUTH
import com.saltario.scribo.utilits.USER
import com.saltario.scribo.utilits.replaceActivity
import com.saltario.scribo.utilits.replaceFragment
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
        initListeners()
    }

    private fun initListeners() {
        settings_btn_change_username.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
    }

    private fun initFields() {
        settings_bio.text = USER.bio
        settings_fullname.text = USER.fullname
        settings_phone_number.text = USER.phone
        settings_status.text = USER.status
        settings_username.text = USER.username
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_action_menu_exit -> {
                AUTH.signOut()
                (activity as MainActivity).replaceActivity(RegisterActivity())
            }
            R.id.settings_action_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }
}