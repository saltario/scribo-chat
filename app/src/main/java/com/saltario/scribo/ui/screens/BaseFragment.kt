package com.saltario.scribo.ui.screens

import androidx.fragment.app.Fragment
import com.saltario.scribo.utilits.hideKeyboard
import com.saltario.scribo.utilits.hideNavBottom
import com.saltario.scribo.utilits.showNavBottom

open class BaseFragment(layout: Int) : Fragment(layout){

    override fun onStart() {
        super.onStart()

        hideNavBottom()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        showNavBottom()
    }
}