package com.saltario.scribo.ui.screens

import androidx.fragment.app.Fragment
import com.saltario.scribo.utilits.hideKeyboard

open class BaseFragment(layout: Int) : Fragment(layout){

    override fun onStart() {
        super.onStart()

        hideKeyboard()
    }
}