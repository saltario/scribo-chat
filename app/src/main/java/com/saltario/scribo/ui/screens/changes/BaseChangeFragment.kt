package com.saltario.scribo.ui.screens.changes

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.saltario.scribo.R
import com.saltario.scribo.utilits.APP_ACTIVITY
import com.saltario.scribo.utilits.hideKeyboard
import com.saltario.scribo.utilits.hideNavBottom
import com.saltario.scribo.utilits.showNavBottom

open class BaseChangeFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()

        setHasOptionsMenu(true)
        hideKeyboard()
        hideNavBottom()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
        showNavBottom()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.action_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }

        return true
    }

    open fun change() {}
}