package com.saltario.scribo.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.saltario.scribo.MainActivity
import com.saltario.scribo.R

open class BaseChangeFragment(layout: Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()

        (activity as MainActivity).mAppDrawer.disableDrawer()
        setHasOptionsMenu(true)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_confirm_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings_confirm_change -> change()
        }

        return true
    }

    open fun change() {}

}